package ru.practicum.shareit.item.mapper;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.ArrayList;
import java.util.Optional;

@Component
@AllArgsConstructor
public class ItemMapper {


    public ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                Optional.ofNullable(item.getRequest()).map(ItemRequest::getId).orElse(null)
        );
    }

    public ItemWithBookingDto toItemWithBookingDto(Item item) {
        return new ItemWithBookingDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                Optional.ofNullable(item.getRequest()).map(ItemRequest::getId).orElse(null),
                null,
                null,
                new ArrayList<>()
        );
    }

    public Item toItem(ItemDto itemDto, User user) {
        return new Item(
                itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                user,
                itemDto.getRequestId() == null ? null : ItemRequest.builder().id(itemDto.getRequestId()).build());

    }


}
