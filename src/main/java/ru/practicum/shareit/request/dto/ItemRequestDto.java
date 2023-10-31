package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor

public class ItemRequestDto {
    private long id;
    private String description;
    private Long requester;
    private LocalDateTime created;
    private List<ItemDto> items;
}
