package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
    private final ItemRequestRepository itemRequestRepository;

    @Transactional
    public ItemRequestDto create(Long userId, ItemRequestDto itemRequestDto) {
        Optional<User> user = userRepository.findById(userId);
        itemRequestDto.setCreated(LocalDateTime.now());
        if (!user.isPresent()) {
            throw new UserIdException("Такого пользователя не существует");
        }
        ItemRequest itemRequest = itemRequestRepository.save(ItemRequestMapper.toItemRequest(itemRequestDto, user.get()));

        return ItemRequestMapper.toItemRequestDto(itemRequest);
    }

    @Transactional(readOnly = true)
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
            ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(request);
            List<ItemDto> itemsDto = new ArrayList<>();
            for (Item item : items) {
                itemsDto.add(ItemMapper.toItemDto(item));
            }
            itemRequestDto.setItems(itemsDto);
            itemRequestsDto.add(itemRequestDto);
        }
        return itemRequestsDto;
    }

    @Transactional(readOnly = true)
    public List<ItemRequestDto> getAll(Long userId, Long from, Long size) {
        List<ItemRequestDto> itemRequestsDto = new ArrayList<>();
        PageRequest pageRequest = PageRequest.of(from.intValue() / size.intValue(), size.intValue(),
                Sort.Direction.DESC, "created");
        List<ItemRequest> itemRequests = itemRequestRepository.findAllNotRequesterId(userId, pageRequest);
        if (itemRequests.isEmpty()) {
            return new ArrayList<>();
        }
        for (ItemRequest itemRequest : itemRequests) {
            itemRequestsDto.add(toItemRequestWithItem(itemRequest));
        }
        return itemRequestsDto;
    }

    @Transactional(readOnly = true)
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
        ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest);
        List<Item> items = itemRepository.findAllByRequest_id(itemRequest.getRequester().getId());
        List<ItemDto> itemsDto = new ArrayList<>();
        for (Item item : items) {
            itemsDto.add(ItemMapper.toItemDto(item));
        }
        itemRequestDto.setItems(itemsDto);
        return itemRequestDto;
    }

}
