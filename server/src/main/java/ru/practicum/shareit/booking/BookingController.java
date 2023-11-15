package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoOut;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@Slf4j
@AllArgsConstructor
@Validated
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingDtoOut create(@Valid @RequestBody BookingDto bookingDto, @RequestHeader("X-Sharer-User-Id") Long booker) {
        log.info("Получен POST-запрос на добавление бронирования владельцем с id={}", booker);

        return bookingService.create(bookingDto, booker);
    }

    @PatchMapping(value = "/{bookingId}")
    public BookingDtoOut update(@RequestHeader("X-Sharer-User-Id") Long userId,
                                @PathVariable Long bookingId,
                                @RequestParam boolean approved) {
        return bookingService.updateBooking(userId, bookingId, approved);
    }

    @GetMapping(value = "/{bookingId}")
    public BookingDtoOut find(@RequestHeader("X-Sharer-User-Id") Long userId,
                              @PathVariable Long bookingId) {
        return bookingService.findBooking(userId, bookingId);
    }

    @GetMapping
    public List<BookingDtoOut> findAllBookingsByBooker(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(defaultValue = "ALL") String state,
            @RequestParam(defaultValue = "0")  long from,
            @RequestParam(defaultValue = "10")  long size) {
        return bookingService.findAllBookingsByBooker(userId, state, from, size);
    }

    @GetMapping(value = "/owner")
    public List<BookingDtoOut> findAllByOwner(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(defaultValue = "ALL") String state,
            @RequestParam(defaultValue = "0")  long from,
            @RequestParam(defaultValue = "10")  long size) {
        return bookingService.findAllBookingsByOwner(userId, state, from, size);
    }
}
