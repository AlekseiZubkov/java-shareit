package ru.practicum.shareit.item.dao;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemInMemoryStorageDao {

    Item create(Item item);

    Item update(Item item);

    void delete(long id);

    Item getItemById(long id);

    List<Item> getAllItemOwner(Long idOwner);

    List<Item> getItemsBySearch(String text);
}
