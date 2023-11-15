package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";
    private final ItemService itemService;

    @GetMapping
    public List<ItemWithBookingDto> getItemsByOwner(@RequestHeader(USER_ID_HEADER) Long idOwner) {
        log.info("Получен GET-запрос получение всех вещей владельца с ID={}", idOwner);
        return itemService.getAllItemOwner(idOwner);
    }

    @GetMapping("/{itemId}")
    public ItemWithBookingDto find(@RequestHeader(USER_ID_HEADER) Long userId,
                                   @PathVariable Long itemId) {
        return itemService.getItemById(itemId, userId);
    }


    @PostMapping
    public ItemDto create(@Valid @RequestBody ItemDto itemDto, @RequestHeader(USER_ID_HEADER) Long ownerId) {
        log.info("Получен POST-запрос на добавление вещи владельцем с id={}", ownerId);

        return itemService.create(itemDto, ownerId);
    }


    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestBody ItemDto itemDto, @PathVariable Long itemId,
                          @RequestHeader(USER_ID_HEADER) Long ownerId) {
        log.info("Получен Patch-запрос на изменение вещи владельцем с id={} вещи с id={}", ownerId, itemId);

        return itemService.update(itemDto, itemId, ownerId);
    }

    @GetMapping("/search")
    public List<ItemDto> getItemsBySearchQuery(@RequestParam String text) {
        log.info("Получен GET-запрос  поиск вещи с текстом={}", text);
        return itemService.getItemsBySearch(text);
    }

    @DeleteMapping("/{itemId}")
    public void delete(@PathVariable Long itemId) {
        log.info("Получен Delete-запрос на удаление вещи с id={}", itemId);
        itemService.delete(itemId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@RequestHeader(USER_ID_HEADER) Long userId,
                                    @PathVariable Long itemId,
                                    @RequestBody CommentDto commentDto) {
        return itemService.createComment(userId, itemId, commentDto);
    }
}

