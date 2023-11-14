package ru.practicum.shareit.booking.exeption;

public class BookingNotOwnerException extends RuntimeException {
    public BookingNotOwnerException(String message) {
        super(message);
    }
}
