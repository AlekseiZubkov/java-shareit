package ru.practicum.shareit.request.exeption;

public class ItemRequestParamException extends RuntimeException {
    public ItemRequestParamException(String message) {
        super(message);
    }
}