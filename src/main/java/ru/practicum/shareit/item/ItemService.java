package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dao.BookingJpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.dao.CommentJpaRepository;
import ru.practicum.shareit.item.dao.ItemJpaRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemBooking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;
import ru.practicum.shareit.item.exeption.CommentException;
import ru.practicum.shareit.item.exeption.ItemIdException;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dao.UserJpaRepository;
import ru.practicum.shareit.user.exeption.UserIdException;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
    private final CommentJpaRepository commentRepository;
    private final ItemMapper itemMapper;
    private final CommentMapper commentMapper;

    public ItemWithBookingDto getItemById(Long itemId, Long userId) {
        Optional<Item> item = itemRepository.findById(itemId);
        if (item.isPresent()) {
            ItemWithBookingDto itemDto = itemMapper.toItemWithBookingDto(item.get());
            if (checkItemOwner(itemId, userId)) {
                itemDto.setLastBooking(getBookingLast(itemId));
                itemDto.setNextBooking(getBookingNext(itemId));

            }
            List<Comment> comments = commentRepository.findAllByItem_Id(itemId);
            if (!comments.isEmpty()) {
                List<CommentDto> commentDtoAll = new ArrayList<>();
                for (Comment comment : comments) {
                    CommentDto commentDto = commentMapper.toCommentDto(comment);
                    commentDtoAll.add(commentDto);
                }
                itemDto.setComments(commentDtoAll);
            }
            return itemDto;
        } else throw new ItemIdException("Вещь не найдена");
    }

    private ItemBooking getBookingLast(Long itemId) {
        List<Booking> bookingLast = bookingRepository.findFirstByItem_IdAndStatusAndEndBeforeOrderByEndDesc(itemId,
                Status.APPROVED, LocalDateTime.now());
        ItemBooking itemBookingLast = new ItemBooking();
        if (!bookingLast.isEmpty()) {
            itemBookingLast = new ItemBooking(bookingLast.get(0).getId(), bookingLast.get(0).getBooker().getId());
            return itemBookingLast;
        } else {
            return null;
        }

    }

    private ItemBooking getBookingNext(Long itemId) {
        List<Booking> bookingNext = bookingRepository.findFirstByItem_IdAndStatusAndEndAfterOrderByStartAsc(itemId,
                Status.APPROVED, LocalDateTime.now());
        ItemBooking itemBookingNext = new ItemBooking();
        if (!bookingNext.isEmpty()) {
            itemBookingNext = new ItemBooking(bookingNext.get(0).getId(), bookingNext.get(0).getBooker().getId());
            return itemBookingNext;
        } else {
            return null;
        }

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
        Optional<Item> item = itemRepository.findById(idItem);
        if (item.get().getOwner().getId().equals(idOwner)) {
            return true;
        }
        return false;
    }

    public List<ItemWithBookingDto> getAllItemOwner(Long idUser) {
        List<Item> items = itemRepository.findAllByOwner_IdOrderById(idUser);
        if (!items.isEmpty()) {
            List<ItemWithBookingDto> ItemsDto = items.stream()
                    .map(itemMapper::toItemWithBookingDto)
                    .collect(toList());

            for (ItemWithBookingDto item : ItemsDto) {
                if (idUser == items.get(0).getOwner().getId()) {
                    item.setLastBooking(getBookingLast(item.getId()));
                    item.setNextBooking(getBookingNext(item.getId()));
                } else {

                }

            }
            return ItemsDto;
        } else throw new ItemIdException("Вещь не найдена");
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

    public CommentDto createComment(Long userId, Long itemId, CommentDto commentDto) {
        if (checkPermitAddComment(userId, itemId, commentDto)) {
            Item item = itemRepository.findById(itemId).get();
            User user = userRepository.findById(userId).get();
            return commentMapper.toCommentDto(commentRepository.save(commentMapper.toComment(commentDto,
                    item, user)));
        } else throw new CommentException("Пользователь не бронировал ранее вещ");
    }

    private boolean checkPermitAddComment(Long userId, Long itemId, CommentDto commentDto) {
        List<Booking> booking = bookingRepository.findByItem_IdAndEndBeforeAndStatusAndBooker_Id(itemId
                , LocalDateTime.now(), Status.APPROVED, userId);
        if (!booking.isEmpty()) {
            return true;
        } else {
            return false;
        }

    }
}
