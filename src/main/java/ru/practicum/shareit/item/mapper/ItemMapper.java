package ru.practicum.shareit.item.mapper;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dao.UserJpaRepository;

import java.util.ArrayList;
import java.util.Optional;

@Component
@AllArgsConstructor
public class ItemMapper {
    private final UserJpaRepository userRepository;

    public ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequest()
        );
    }

    public ItemWithBookingDto toItemWithBookingDto(Item item) {
        return new ItemWithBookingDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequest(),
                null,
                null,
                new ArrayList<>()
        );
    }

    public Item toItem(ItemDto itemDto, Long ownerId) {
        return new Item(
                itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                findUser(ownerId),
                itemDto.getRequest() != null ? itemDto.getRequest() : null
        );
    }

    private User findUser(Long ownerId) {
        Optional<User> user = userRepository.findById(ownerId);
        return user.get();
    }

}