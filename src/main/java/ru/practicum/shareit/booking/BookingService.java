package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dao.BookingJpaRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.exeption.BookingException;
import ru.practicum.shareit.booking.exeption.BookingNotOwnerException;
import ru.practicum.shareit.booking.exeption.StateException;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.dao.ItemJpaRepository;
import ru.practicum.shareit.item.exeption.ItemIdException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dao.UserJpaRepository;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;



@Service
@Slf4j
@AllArgsConstructor
public class BookingService {
    private final ItemJpaRepository itemRepository;
    private final UserJpaRepository userRepository;
    private final BookingJpaRepository bookingRepository;
    private BookingMapper bookingMapper;


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
        Optional<User> booker = userRepository.findById(bookerId);
        Booking newBooking = new Booking();
        newBooking.setStatus(Status.WAITING);
        newBooking.setBooker(booker.get());
        newBooking.setItem(item.get());
        newBooking.setStart(bookingDto.getStart());
        newBooking.setEnd(bookingDto.getEnd());
        return BookingMapper.toBookingDtoOut(bookingRepository.save(newBooking));
    }

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
            throw new ItemIdException("Вещ не найдена");
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


    public List<BookingDtoOut> findAllBookingsByBooker(Long userId, String state) {
        List<Booking> bookings = getBookingsFromState(bookingRepository.findByBookerIdOrderByStartDesc(userId),state);

        if (bookings.isEmpty()) {
            throw new BookingException("Не найдено бронирований у этого пользователя");
        }
        List<BookingDtoOut> bookingDtoOut = new ArrayList<>();
        for (Booking booking : bookings) {
            bookingDtoOut.add(BookingMapper.toBookingDtoOut(booking));
        }
        return bookingDtoOut;
    }

    public List<BookingDtoOut> findAllBookingsByOwner(Long userId, String state) {
        List<Booking> bookings = getBookingsFromState(bookingRepository.findByItem_Owner_IdOrderByStartDesc(userId),state);
        if (bookings.isEmpty()) {
            throw new BookingException("Не найдено бронирований у этого пользователя");
        }
        List<BookingDtoOut> bookingDtoOut = new ArrayList<>();
        for (Booking booking : bookings) {
            bookingDtoOut.add(BookingMapper.toBookingDtoOut(booking));
        }

        return bookingDtoOut;
    }

    private List<Booking> getBookingsFromState(List<Booking> bookings, String state) {
        if (state.equals("ALL")) {
            return bookings;
        } else if (state.equals("CURRENT")) {
            return bookings.stream()
                    .filter(booking -> booking.getStatus().equals(Status.APPROVED))
                    .collect(Collectors.toList());
        } else if (state.equals("PAST")) {
            return bookings.stream()
                    .filter(booking -> booking.getStatus().equals(Status.APPROVED) ||
                            booking.getEnd().isAfter(LocalDateTime.now()))
                    .collect(Collectors.toList());
        } else if (state.equals("FUTURE")) {
            return bookings.stream()
                    .filter(booking -> booking.getStart().isAfter(LocalDateTime.now()))
                    .collect(Collectors.toList());
        } else if (state.equals("WAITING")) {
            return bookings.stream()
                    .filter(booking -> booking.getStatus().equals(Status.WAITING))
                    .collect(Collectors.toList());
        } else if (state.equals("REJECTED")) {
            return bookings.stream()
                    .filter(booking -> booking.getStatus().equals(Status.REJECTED) ||
                            booking.getStatus().equals(Status.CANCELED))
                    .collect(Collectors.toList());
        } else {
            throw new StateException("Unknown state: " + state);

        }
    }

}
