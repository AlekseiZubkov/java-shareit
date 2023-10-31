package ru.practicum.shareit.request.exeption;

public class ItemRequestException extends RuntimeException {
    public ItemRequestException(String message) {
        super(message);
    }
}
