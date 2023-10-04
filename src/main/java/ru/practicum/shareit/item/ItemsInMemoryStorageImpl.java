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
    public List<Item> getAll() {
        return new ArrayList<>(items.values());
    }

    @Override
    public Item create(Item item) {
        item.setId(idItems++);
        items.put(item.getId(), item);
        System.out.println("Items " + items);

        return item;
    }

    @Override
    public Item update(Item item, Long idItem, Long idOwner) {
        if (!items.containsKey(idItem)) {
            throw new UserIdException("Id вещи не найден");
        }
        if (item.getId() == (null)) {
            item.setId(idItem);
        }
        if (item.getName() == null) {
            item.setName(items.get(idItem).getName());
        }
        if (item.getDescription() == null) {
            item.setDescription(items.get(idItem).getDescription());
        }

        if (item.getAvailable() == null) {
            item.setAvailable(items.get(idItem).getAvailable());
        }
        items.put(idItem, item);

        return item;
    }

    @Override
    public void delete(long id) {

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
        List<Item> ItemSearch = new ArrayList<>();
        if (!text.isEmpty()) {
            ItemSearch = items.values().stream().
                    filter(item -> item.getAvailable().equals(true)).
                    filter(item -> item.getName().toLowerCase().contains(finalText) ||
                            item.getDescription().toLowerCase().contains(finalText))
                    .collect(toList());
        }
        return ItemSearch;
    }
}


