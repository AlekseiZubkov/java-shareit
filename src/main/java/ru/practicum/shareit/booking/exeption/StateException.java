package ru.practicum.shareit.booking.exeption;

public class StateException extends RuntimeException {
    public StateException(String message) {
        super(message);
    }
    public StateException() {
    }

    public StateException(String message, Throwable cause) {
        super(message, cause);
    }

    public StateException(Throwable cause) {
        super(cause);
    }
}
