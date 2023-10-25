package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

@Component
@AllArgsConstructor
public class BookingMapper {


    public static Booking toBooking(BookingDto bookingDto, Item item, User booker, Status status) {
        return new Booking(
                null,
                bookingDto.getStart(),
                bookingDto.getEnd(),
                item,
                booker,
                status

        );
    }

    public static BookingDtoOut toBookingDtoOut(Booking booking) {
        return BookingDtoOut.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(booking.getItem())
                .booker(booking.getBooker())
                .status(booking.getStatus())
                .build();

    }
}
