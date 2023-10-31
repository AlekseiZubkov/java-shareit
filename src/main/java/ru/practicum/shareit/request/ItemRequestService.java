package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dao.BookingJpaRepository;
import ru.practicum.shareit.item.dao.ItemJpaRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dao.UserJpaRepository;
import ru.practicum.shareit.user.exeption.UserIdException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class ItemRequestService {
    private final ItemJpaRepository itemRepository;
    private final UserJpaRepository userRepository;
    private final BookingJpaRepository bookingRepository;
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRequestMapper itemRequestMapper;

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
        List<ItemRequest> ItemRequests = itemRequestRepository.findAllByRequesterId(userId);
        if (ItemRequests.isEmpty()) {
            return new ArrayList<>();
        }
        List<ItemRequestDto> itemRequestsDto = new ArrayList<>();
        for (ItemRequest request : ItemRequests) {
            ItemRequestDto itemRequestDto = itemRequestMapper.toItemRequestDto(request);
            itemRequestDto.setItem(itemRepository.findAllByRequest_id(request.getId()));
            itemRequestsDto.add(itemRequestDto);
        }

        return itemRequestsDto;
    }


    public List<ItemRequestDto> getAll(Long userId, Long from, Long size) {
        List<ItemRequestDto> itemRequestsDto = new ArrayList<>();
        PageRequest pageRequest = PageRequest.of(from.intValue(), size.intValue());
        List<ItemRequest> itemRequests = itemRequestRepository.findAllByOrderByCreatedDesc(pageRequest);
        if (itemRequests.isEmpty()) {
            return new ArrayList<>();
        }
        for (ItemRequest request : itemRequests) {
            itemRequestsDto.add(itemRequestMapper.toItemRequestDto(request));
        }
        return itemRequestsDto;
    }

    public ItemRequestDto getByID(Long userId, Long requestId) {
        return null;
    }

/*    private List<Item> getItems(Long ItemId) {
        return itemRepository.findAllByRequest_id(ItemId);
    }
    public List<Item> filterItemsById(List<Item> itemList, long requestId) {
        return itemList.stream()
                .filter(item -> item.getRequest().getId() == requestId)
                .collect(Collectors.toList());
    }*/
}
