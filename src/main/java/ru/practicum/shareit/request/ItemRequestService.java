package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dao.BookingJpaRepository;
import ru.practicum.shareit.item.dao.ItemJpaRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.exeption.ItemRequestParamException;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dao.UserJpaRepository;
import ru.practicum.shareit.user.exeption.UserIdException;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
public class ItemRequestService {
    private final ItemJpaRepository itemRepository;
    private final UserJpaRepository userRepository;
    private final BookingJpaRepository bookingRepository;
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRequestMapper itemRequestMapper;
    private final ItemMapper itemMapper;

    public ItemRequestDto create(Long userId, ItemRequestDto itemRequestDto) {
        Optional<User> user = userRepository.findById(userId);
        itemRequestDto.setCreated(LocalDateTime.now());
        if (!user.isPresent()) {
            throw new UserIdException("Такого пользователя не существует");
        }
        ItemRequest itemRequest = itemRequestRepository.save(itemRequestMapper.toItemRequest(itemRequestDto, user.get()));

        return itemRequestMapper.toItemRequestDto(itemRequest);
    }

    public List<ItemRequestDto> get(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (!user.isPresent()) {
            throw new UserIdException("Такого пользователя не существует");
        }
        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequesterId(userId);
        if (itemRequests.isEmpty()) {
            return new ArrayList<>();
        }
        List<ItemRequestDto> itemRequestsDto = new ArrayList<>();
        List<Item> items = itemRepository.findAllByRequest_id(userId);
        for (ItemRequest request : itemRequests) {
            ItemRequestDto itemRequestDto = itemRequestMapper.toItemRequestDto(request);
            List<ItemDto> itemsDto = new ArrayList<>();
            for (Item item : items) {
                itemsDto.add(itemMapper.toItemDto(item));
            }
            itemRequestDto.setItems(itemsDto);
            itemRequestsDto.add(itemRequestDto);
        }
        return itemRequestsDto;
    }


    public List<ItemRequestDto> getAll(Long userId, Long from, Long size) {
        checkParamRequest(from, size);
        List<ItemRequestDto> itemRequestsDto = new ArrayList<>();
        PageRequest pageRequest = PageRequest.of(from.intValue() / size.intValue(), size.intValue(),
                Sort.Direction.DESC, "created");
        List<ItemRequest> itemRequests = itemRequestRepository.findAll(userId, pageRequest);
        if (itemRequests.isEmpty()) {
            return new ArrayList<>();
        }
        for (ItemRequest itemRequest : itemRequests) {
            itemRequestsDto.add(toItemRequestWithItem(itemRequest));
        }
        return itemRequestsDto;
    }

    public ItemRequestDto getByID(Long userId, Long requestId) {
        Optional<User> user = userRepository.findById(userId);
        Optional<ItemRequest> itemRequest = itemRequestRepository.findById(requestId);
        if (!user.isPresent()) {
            throw new UserIdException("Такого пользователя не существует");
        }
        if (!itemRequest.isPresent()) {
            throw new ItemRequestParamException("Такого запроса не существует");
        }
        ItemRequestDto itemRequestDto = toItemRequestWithItem(itemRequest.get());

        return itemRequestDto;
    }

    private ItemRequestDto toItemRequestWithItem(ItemRequest itemRequest) {
        ItemRequestDto itemRequestDto = itemRequestMapper.toItemRequestDto(itemRequest);
        List<Item> items = itemRepository.findAllByRequest_id(itemRequest.getRequester().getId());
        List<ItemDto> itemsDto = new ArrayList<>();
        for (Item item : items) {
            itemsDto.add(itemMapper.toItemDto(item));
        }
        itemRequestDto.setItems(itemsDto);
        return itemRequestDto;
    }

    private void checkParamRequest(Long from, Long size) {
        if (from < 0 || size <= 0) {
            throw new ValidationException("Параметры запроса отрицательные");
        }
        if (from == 0 && size == 0) {
            throw new ValidationException("Параметры запроса равны 0");
        }

    }


   /* private List<Item> getItems(Long ItemId) {
        return itemRepository.findAllByRequest_id(ItemId);
    }
    public List<Item> filterItemsById(List<Item> itemList, long requestId) {
        return itemList.stream()
                .filter(item -> item.getRequest().getId() == requestId)
                .collect(Collectors.toList());
    }*/
}
