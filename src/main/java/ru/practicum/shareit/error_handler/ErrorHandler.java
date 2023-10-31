package ru.practicum.shareit.error_handler;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.booking.exeption.BookingException;
import ru.practicum.shareit.booking.exeption.BookingNotOwnerException;
import ru.practicum.shareit.booking.exeption.StateException;
import ru.practicum.shareit.item.exeption.CommentException;
import ru.practicum.shareit.item.exeption.ItemIdException;
import ru.practicum.shareit.request.exeption.ItemRequestException;
import ru.practicum.shareit.request.exeption.ItemRequestParamException;
import ru.practicum.shareit.user.exeption.EmailException;
import ru.practicum.shareit.user.exeption.UserIdException;

import javax.validation.ValidationException;


@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleEmailException(EmailException e) {
        return new ErrorResponse("Адрес почты занят ", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleItemRequestException(ItemRequestException e) {
        return new ErrorResponse("Неверный запрос", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleUserIdException(UserIdException e) {
        return new ErrorResponse("Недопустимое id", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleItemIdException(ItemIdException e) {
        return new ErrorResponse("Недопустимое id вещи", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidUserException(final ValidationException e) {
        return new ErrorResponse("Ошибка валидации", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleStateException(final StateException e) {
        return new ErrorResponse("Unknown state: UNSUPPORTED_STATUS", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleBookingException(final BookingException e) {
        return new ErrorResponse("Ошибка бронирования", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleBookingNotOwnerException(final BookingNotOwnerException e) {
        return new ErrorResponse("Пользователь не является хозяином вещи", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleCommentException(final CommentException e) {
        return new ErrorResponse("Комментарий не может быть оставлен", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleItemRequestParamException(final ItemRequestParamException e) {
        return new ErrorResponse("Ошибка запроса", e.getMessage());
    }
}
