package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dao.BookingJpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.dao.ItemJpaRepository;
import ru.practicum.shareit.item.dto.ItemBooking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;
import ru.practicum.shareit.item.exeption.ItemIdException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dao.UserJpaRepository;
import ru.practicum.shareit.user.exeption.UserIdException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@Service
@Slf4j
@AllArgsConstructor
public class ItemService {
    private final ItemJpaRepository itemRepository;
    private final UserJpaRepository userRepository;
    private final BookingJpaRepository bookingRepository;
    private final ItemMapper itemMapper;


    public ItemWithBookingDto getItemById(Long itemId, Long userId) {
        Optional<Item> item = itemRepository.findById(itemId);
        if (item.isPresent()) {
            ItemWithBookingDto ItemDto = itemMapper.toItemWithBookingDto(item.get());
            if (checkItemOwner(itemId, userId)) {
                ItemDto.setLastBooking(getBookingLast(itemId));
                ItemDto.setNextBooking(getBookingNext(itemId));
            }
            return ItemDto;
        } else throw new ItemIdException("Вещь не найдена");
    }

    private ItemBooking getBookingLast(Long itemId) {
        List<Booking> bookingLast = bookingRepository.findFirstByItem_IdAndStatusAndEndBeforeOrderByEndDesc(itemId,
                Status.APPROVED, LocalDateTime.now());
        ItemBooking itemBookingLast = new ItemBooking();
        if (!bookingLast.isEmpty()) {
            itemBookingLast = new ItemBooking(bookingLast.get(0).getId(), bookingLast.get(0).getBooker().getId());
        }
        return itemBookingLast;
    }

    private ItemBooking getBookingNext(Long itemId) {
        List<Booking> bookingNext = bookingRepository.findFirstByItem_IdAndStatusAndEndAfterOrderByStartAsc(itemId,
                Status.APPROVED, LocalDateTime.now());
        ItemBooking itemBookingNext = new ItemBooking();
        if (!bookingNext.isEmpty()) {
            itemBookingNext = new ItemBooking(bookingNext.get(0).getId(), bookingNext.get(0).getBooker().getId());
        }
        return itemBookingNext;
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
        if (text.isEmpty()) {
            return List.of();
        }
        String finalText = text.toLowerCase();
        return itemRepository.findAll().stream()
                .filter(item -> item.getAvailable().equals(true)) //Фильтрация элементов по условию Available
                .filter(item -> item.getName().toLowerCase().contains(finalText) ||
                        item.getDescription().toLowerCase().contains(finalText)) // проверка имени, описания на finalText
                .map(itemMapper::toItemDto)
                .collect(toList());
    }
}
