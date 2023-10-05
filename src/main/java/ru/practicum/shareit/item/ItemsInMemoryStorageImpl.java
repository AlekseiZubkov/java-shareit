package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dao.ItemInMemoryStorageDao;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.exeption.UserIdException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

@Component
@Slf4j
@NoArgsConstructor
@AllArgsConstructor
public class ItemsInMemoryStorageImpl implements ItemInMemoryStorageDao {
    private final Map<Long, Item> items = new HashMap<>();
    private ItemMapper itemMapper;
    private long idItems = 1;

    @Override
    public List<ItemDto> getAll() {
        List<ItemDto> itemsDto = new ArrayList<>();
        for (Item item : items.values()) {
            itemsDto.add(itemMapper.toItemDto(item));
        }
        return itemsDto;
    }


    @Override
    public Item create(Item item) {
        item.setId(idItems++);
        items.put(item.getId(), item);
        System.out.println("Items " + items);

        return item;
    }

    @Override
    public Item update(ItemDto itemDto, Long idItem, Long idOwner) {
        Item item = items.get(idItem);
        if (!items.containsKey(idItem)) {
            throw new UserIdException("Id вещи не найден");
        }
        if (itemDto.getId() == null) {
            item.setId(idItem);
        }
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        System.out.println("item.getAvailable() " + item.getAvailable());
        System.out.println("itemDto.getAvailable() " + itemDto.getAvailable());
        if (item.getAvailable() != itemDto.getAvailable() && itemDto.getAvailable() != null) {
            System.out.println("oooooooooooooooooooooooooooooo");
            item.setAvailable(itemDto.getAvailable());
        }
        System.out.println("++++++++++++++item " + item);
        return item;
    }

    @Override
    public void delete(long id) {
        items.remove(id);
    }

    @Override
    public Item getItemById(long id) {
        return items.get(id);
    }

    @Override
    public List<Item> getAllItemOwner(Long idOwner) {
        List<Item> resultItems = new ArrayList<>();
        for (Item item : items.values()) {
            if (item.getOwner().equals(idOwner)) {
                resultItems.add(item);
            }
        }
        return resultItems;
    }

    @Override
    public List<Item> getItemsBySearch(String text) {
        String finalText = text.toLowerCase();
        List<Item> itemSearch = new ArrayList<>();
        if (!text.isEmpty()) {
            itemSearch = items.values().stream()
                    .filter(item -> item.getAvailable().equals(true))
                    .filter(item -> item.getName().toLowerCase().contains(finalText) ||
                            item.getDescription().toLowerCase().contains(finalText)).collect(toList());
        }

        return itemSearch;
    }
}



