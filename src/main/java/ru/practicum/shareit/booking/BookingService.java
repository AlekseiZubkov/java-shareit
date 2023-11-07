package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dao.BookingJpaRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.exeption.BookingException;
import ru.practicum.shareit.booking.exeption.BookingNotOwnerException;
import ru.practicum.shareit.booking.exeption.StateException;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.dao.ItemJpaRepository;
import ru.practicum.shareit.item.exeption.ItemIdException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dao.UserJpaRepository;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@Slf4j
@AllArgsConstructor
public class BookingService {
    private final ItemJpaRepository itemRepository;
    private final UserJpaRepository userRepository;
    private final BookingJpaRepository bookingRepository;

    @Transactional
    public BookingDtoOut create(BookingDto bookingDto, Long bookerId) {

        Optional<Item> item = itemRepository.findById(bookingDto.getItemId());
        if (!item.isPresent()) {
            throw new ItemIdException("Вещь не найдена"); //
        }
        if (!item.get().getAvailable()) {
            throw new ValidationException("Вещь не доступна для бронирования "); // Проверяем что вещь доступна
        }
        if (bookingDto.getEnd().isBefore(bookingDto.getStart()) || bookingDto.getEnd().equals(bookingDto.getStart())) {
            throw new ValidationException("Не верное время бронирования");
        }
        if (Objects.equals(item.get().getOwner().getId(), bookerId)) {
            throw new BookingException("Предмет не может быть взят в аренду у себя");
        }
        User booker = userRepository.findById(bookerId).get();
        Booking newBooking = BookingMapper.toBooking(bookingDto, item.get(), booker, Status.WAITING);

        bookingRepository.save(newBooking);
        return BookingMapper.toBookingDtoOut(newBooking);
    }

    @Transactional
    public BookingDtoOut updateBooking(Long ownerId, Long bookingId, boolean approved) {
        Optional<Booking> booking = bookingRepository.findById(bookingId);
        if (booking.isPresent()) {
            Booking updatedBooking = booking.get();
            if ((updatedBooking.getStatus().equals(Status.APPROVED)
                    || updatedBooking.getStatus().equals(Status.REJECTED))
            ) {
                throw new ValidationException("Статус уже обновлен");
            }
            if (isOwner(ownerId, updatedBooking)) {
                if (approved) {
                    updatedBooking.setStatus(Status.APPROVED);
                } else {
                    updatedBooking.setStatus(Status.REJECTED);
                }
                return BookingMapper.toBookingDtoOut(bookingRepository.save(updatedBooking));
            } else {
                throw new BookingNotOwnerException("Пользователь не владелец вещи");
            }
        } else {
            throw new BookingException("Бронирование не найдено");
        }

    }

    private Boolean isOwner(Long userId, Booking booking) {
        List<Item> items = itemRepository.findAllByOwner_Id(userId);
        for (Item item : items) {
            if (item.getOwner().equals(booking.getItem().getOwner())) {
                return true;
            }
        }
        return false;
    }

    @Transactional(readOnly = true)
    public BookingDtoOut findBooking(Long userId, Long bookingId) {
        Optional<Booking> booking = bookingRepository.findById(bookingId);
        if (booking.isPresent()) {
            if (isOwner(userId, booking.get()) || userId.equals(booking.get().getBooker().getId())) {
                return BookingMapper.toBookingDtoOut(booking.get());
            } else {
                throw new BookingNotOwnerException("Пользователь не является хозяином или арендатором вещи");
            }
        } else {
            throw new BookingException("Бронирования не существует");
        }
    }

    @Transactional(readOnly = true)
    public List<BookingDtoOut> findAllBookingsByBooker(Long userId, String stateStr, Long from, Long size) {
        State state;
        PageRequest pageRequest = PageRequest.of(from.intValue() / size.intValue(), size.intValue(), Sort.Direction.DESC, "start");
        try {
            state = State.valueOf(stateStr);
        } catch (IllegalArgumentException e) {
            throw new StateException("Unknown state: " + stateStr);
        }
        List<Booking> bookingsL = bookingRepository.findByBookerId(userId, pageRequest);
        List<Booking> bookings = getBookingsFromState(bookingsL, state);

        if (bookings.isEmpty()) {
            throw new BookingException("Не найдено бронирований у этого пользователя");
        }
        List<BookingDtoOut> bookingDtoOut = new ArrayList<>();
        for (Booking booking : bookings) {
            bookingDtoOut.add(BookingMapper.toBookingDtoOut(booking));
        }
        return bookingDtoOut;
    }

    @Transactional(readOnly = true)
    public List<BookingDtoOut> findAllBookingsByOwner(Long userId, String stateStr, Long from, Long size) {
        State state;
        PageRequest pageRequest = PageRequest.of(from.intValue() / size.intValue(), size.intValue());
        try {
            state = State.valueOf(stateStr);
        } catch (IllegalArgumentException e) {
            throw new StateException("Unknown state: " + stateStr);
        }
        List<Booking> bookings = getBookingsFromState(bookingRepository.findByItem_Owner_IdOrderByStartDesc(userId, pageRequest), state);
        if (bookings.isEmpty()) {
            throw new BookingException("Не найдено бронирований у этого пользователя");
        }
        List<BookingDtoOut> bookingDtoOut = new ArrayList<>();
        for (Booking booking : bookings) {
            bookingDtoOut.add(BookingMapper.toBookingDtoOut(booking));
        }

        return bookingDtoOut;
    }

    private List<Booking> getBookingsFromState(List<Booking> bookings, State state) {
        switch (state) {
            case ALL:
                return bookings;
            case CURRENT:
                return bookings.stream()
                        .filter(booking ->
                                booking.getStart().isBefore(LocalDateTime.now()) &&
                                        booking.getEnd().isAfter(LocalDateTime.now()))
                        .collect(Collectors.toList());
            case PAST:
                List<Booking> bookingList = bookings.stream()
                        .filter(booking ->
                                booking.getStatus().equals(Status.APPROVED) &&
                                        booking.getEnd().isBefore(LocalDateTime.now()))
                        .collect(Collectors.toList());
                return bookingList;
            case FUTURE:
                return bookings.stream()
                        .filter(booking -> booking.getStart().isAfter(LocalDateTime.now()))
                        .collect(Collectors.toList());
            case WAITING:
                return bookings.stream()
                        .filter(booking -> booking.getStatus().equals(Status.WAITING))
                        .collect(Collectors.toList());
            case REJECTED:
                return bookings.stream()
                        .filter(booking -> booking.getStatus().equals(Status.REJECTED) ||
                                booking.getStatus().equals(Status.CANCELED))
                        .collect(Collectors.toList());
            default:
                throw new StateException("Unknown state: " + state);
        }
    }

}
