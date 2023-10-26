package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dao.ItemInMemoryStorageDao;
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

    private long idItems = 1;


    @Override
    public Item create(Item item) {
        item.setId(idItems++);
        items.put(item.getId(), item);
        System.out.println("Items " + items);

        return item;
    }

    @Override
    public Item update(Item item) {
        Item updateItem = items.get(item.getId());
        if (!items.containsKey(item.getId())) {
            throw new UserIdException("Id вещи не найден");
        }
        if (item.getId() != null) {
            updateItem.setId(item.getId());
        }
        if (item.getName() != null) {
            updateItem.setName(item.getName());
        }
        if (item.getDescription() != null) {
            updateItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != updateItem.getAvailable() && item.getAvailable() != null) {

            updateItem.setAvailable(item.getAvailable());
        }
        return updateItem;
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
            if (item.getOwner().getId().equals(idOwner)) {
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



