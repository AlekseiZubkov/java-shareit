package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dao.ItemJpaRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.exeption.ItemIdException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dao.UserJpaRepository;
import ru.practicum.shareit.user.exeption.UserIdException;

import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@Service
@Slf4j
@AllArgsConstructor
public class ItemService {
    private final ItemJpaRepository itemRepository;
    private final UserJpaRepository userRepository;
    private final ItemMapper itemMapper;



    public ItemDto getItemById(Long itemId) {

        Optional<Item> item = itemRepository.findById(itemId);
        if (item.isPresent()) {
            return itemMapper.toItemDto(item.get());
        } else throw new ItemIdException("Вещь не найдена");
    }

    public ItemDto create(ItemDto itemDto, Long idOwner) {
             checkUserFind(idOwner);
            return itemMapper.toItemDto(itemRepository.save(itemMapper.toItem(itemDto, idOwner)));

    }

    public void delete(Long id) {
        itemRepository.deleteById(id);

    }

    public ItemDto update(ItemDto itemDto, Long idItem, Long idOwner) {
        checkUserFind(idOwner);
        Optional<Item> updateItem = itemRepository.findById(idItem);
/*        if (updateItem.get() != null) {
            throw new UserIdException("Id вещи не найден");
        }*/
        if (itemDto.getId() != null) {
            updateItem.get().setId(itemDto.getId());
        }
        if (itemDto.getName() != null) {
            updateItem.get().setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            updateItem.get().setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != updateItem.get().getAvailable() && itemDto.getAvailable() != null) {
            updateItem.get().setAvailable(itemDto.getAvailable());
        }

        if (!checkItemOwner(idItem, idOwner)) {
            throw new UserIdException("Пользователь с id  = " + idOwner + "не является владельцем вещи с id " + idItem);
        }


        return itemMapper.toItemDto(itemRepository.save(updateItem.get()));
    }

    private void checkUserFind(Long idOwner) {
        Optional<User> user = userRepository.findById(idOwner);
        if (user.isEmpty()) {
            throw new UserIdException("Пользователь с id  = " + idOwner + " не найден");
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
        return itemRepository.findAllByOwner_IdOrderById(idOwner).stream()
                .map(itemMapper::toItemDto)
                .collect(toList());
    }

    public List<ItemDto> getItemsBySearch(String text) {
       if (text.isEmpty()){
           return List.of();
       }
        String finalText = text.toLowerCase();
        return itemRepository.findAll().stream()
                .filter(item -> item.getAvailable().equals(true)) //Фильтрация элементов по условию Available
                .filter(item -> item.getName().toLowerCase().contains(finalText) ||
                        item.getDescription().toLowerCase().contains(finalText)) // проверка имени,описания на finalText
                .map(itemMapper::toItemDto)
                .collect(toList());
    }
}
