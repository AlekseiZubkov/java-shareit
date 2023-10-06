package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dao.ItemInMemoryStorageDao;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UsersInMemoryStorageDao;
import ru.practicum.shareit.user.exeption.UserIdException;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@Slf4j
@AllArgsConstructor
public class ItemService {
    private final ItemInMemoryStorageDao itemStorage;
    private final ItemMapper mapper;
    private final UsersInMemoryStorageDao userStorage;

    public ItemDto getItemById(long itemId) {
        return mapper.toItemDto(itemStorage.getItemById(itemId));
    }

    public ItemDto create(ItemDto itemDto, Long idOwner) {
        checkUserFind(idOwner);
        return mapper.toItemDto(itemStorage.create(mapper.toItem(itemDto, idOwner)));

    }

    public void delete(Long id) {
        itemStorage.delete(id);

    }

    public ItemDto update(ItemDto itemDto, Long idItem, Long idOwner) {
        checkUserFind(idOwner);

        if (!checkItemOwner(idItem, idOwner)) {
            throw new UserIdException("Пользователь с id  = " + idOwner + "не является владельцем вещи с id " + idItem);
        }
        Item item = mapper.toItem(itemDto, idOwner);
        item.setId(idItem);
        return mapper.toItemDto(itemStorage.update(item));
    }

    private void checkUserFind(Long idOwner) {
        if (userStorage.getUserById(idOwner) == null) {
            throw new UserIdException("Пользователь с id  = " + idOwner + "не найден");
        }
    }

    private boolean checkItemOwner(Long idItem, Long idOwner) {
        List<ItemDto> itemsL = getAllItemOwner(idOwner);
        for (ItemDto item : itemsL) {
            if (item.getId().equals(idItem)) {
                return true;
            }
        }
        return false;
    }

    public List<ItemDto> getAllItemOwner(Long idOwner) {
        return itemStorage.getAllItemOwner(idOwner).stream()
                .map(mapper::toItemDto)
                .collect(toList());
    }

    public List<ItemDto> getItemsBySearch(String text) {
        return itemStorage.getItemsBySearch(text).stream()
                .map(mapper::toItemDto)
                .collect(toList());

    }
}
