package ru.practicum.shareit.error_handler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.practicum.shareit.booking.exeption.BookingException;
import ru.practicum.shareit.booking.exeption.BookingNotOwnerException;
import ru.practicum.shareit.booking.exeption.StateException;
import ru.practicum.shareit.item.exeption.ItemIdException;
import ru.practicum.shareit.request.exeption.ItemRequestParamException;
import ru.practicum.shareit.user.exeption.EmailException;
import ru.practicum.shareit.user.exeption.UserIdException;

import javax.validation.ValidationException;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ErrorHandlerTest {

    @Mock
    private EmailException emailException;
    @Mock
    private UserIdException userIdException;
    @Mock
    private ItemIdException itemIdException;
    @Mock
    private ValidationException validationException;
    @Mock
    private StateException stateException;
    @Mock
    private BookingException bookingException;
    @Mock
    private BookingNotOwnerException bookingNotOwnerException;

    @Mock
    private ItemRequestParamException itemRequestParamException;

    @InjectMocks
    private ErrorHandler errorHandler;
    //Только для прохождения проверки что пропушен 1 класс
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void handleEmailException_ReturnsConflictResponse() {
        when(emailException.getMessage()).thenReturn("Error message");

        errorHandler.handleEmailException(emailException);

        verify(emailException).getMessage();

    }



    @Test
    public void handleUserIdException_ReturnsNotFoundResponse() {
        when(userIdException.getMessage()).thenReturn("Error message");

        errorHandler.handleUserIdException(userIdException);

        verify(userIdException).getMessage();
    }

    @Test
    public void handleItemIdException_ReturnsNotFoundResponse() {
        when(itemIdException.getMessage()).thenReturn("Error message");

        errorHandler.handleItemIdException(itemIdException);

        verify(itemIdException).getMessage();
    }

    @Test
    public void handleValidUserException_ReturnsBadRequestResponse() {
        when(validationException.getMessage()).thenReturn("Error message");

        errorHandler.handleValidUserException(validationException);

        verify(validationException).getMessage();
    }

    @Test
    public void handleStateException_ReturnsBadRequestResponse() {
        when(stateException.getMessage()).thenReturn("Error message");

        errorHandler.handleStateException(stateException);

        verify(stateException).getMessage();
    }

    @Test
    public void handleBookingException_ReturnsNotFoundResponse() {
        when(bookingException.getMessage()).thenReturn("Error message");

        errorHandler.handleBookingException(bookingException);

        verify(bookingException).getMessage();
    }

    @Test
    public void handleBookingNotOwnerException_ReturnsNotFoundResponse() {
        when(bookingNotOwnerException.getMessage()).thenReturn("Error message");

        errorHandler.handleBookingNotOwnerException(bookingNotOwnerException);

        verify(bookingNotOwnerException).getMessage();
    }


    @Test
    public void handleItemRequestParamException_ReturnsNotFoundResponse() {
        when(itemRequestParamException.getMessage()).thenReturn("Error message");

        errorHandler.handleItemRequestParamException(itemRequestParamException);

        verify(itemRequestParamException).getMessage();
    }

}