package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    BookingService bookingService;

@SneakyThrows
    @Test
    void update()  {

        Long userId = 1L;
        Long bookingId = 1L;
        boolean approved = true;
        BookingDtoOut bookingDtoOut = BookingDtoOut.builder()
                .id(bookingId)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusHours(1))
                .status(Status.APPROVED)
                .booker(new User())
                .item(new Item())
                .build();


        when(bookingService.updateBooking(userId, bookingId, approved)).thenReturn(bookingDtoOut);


        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId)
                        .param("approved", String.valueOf(approved)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingDtoOut.getId()))
                .andExpect(jsonPath("$.status").value(bookingDtoOut.getStatus().toString()));
        verify(bookingService, times(1)).updateBooking(userId, bookingId, approved);
    }
@SneakyThrows
    @Test
    void find() {

        Long userId = 1L;
        Long bookingId = 1L;
        BookingDtoOut bookingDtoOut = BookingDtoOut.builder()
                .id(bookingId)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusHours(1))
                .status(Status.APPROVED)
                .booker(new User())
                .item(new Item())
                .build();


        when(bookingService.findBooking(userId, bookingId)).thenReturn(bookingDtoOut);


        mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingDtoOut.getId()))

                .andExpect(jsonPath("$.status").value(bookingDtoOut.getStatus().toString()));
        verify(bookingService, times(1)).findBooking(userId, bookingId );
    }
@SneakyThrows
    @Test
    void findAllBookingsByBooker()  {

        Long userId = 1L;
        String state = "ALL";
        Long from = 0L;
        Long size = 10L;
        BookingDtoOut bookingDtoOut = BookingDtoOut.builder()
                .id(1L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusHours(1))
                .status(Status.APPROVED)
                .booker(new User())
                .item(new Item())
                .build();
        List<BookingDtoOut> bookingDtoOutList = Arrays.asList(bookingDtoOut);

        when(bookingService.findAllBookingsByBooker(userId, state, from, size)).thenReturn(bookingDtoOutList);

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", state)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(bookingDtoOut.getId()))
                .andExpect(jsonPath("$[0].status").value(bookingDtoOut.getStatus().toString()));
    }
@SneakyThrows
    @Test
    void findAllByOwner()  {

        Long userId = 1L;
        String state = "ALL";
        Long from = 0L;
        Long size = 10L;
        BookingDtoOut bookingDtoOut = BookingDtoOut.builder()
                .id(1L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusHours(1))
                .status(Status.APPROVED)
                .booker(new User())
                .item(new Item())
                .build();
        List<BookingDtoOut> bookingDtoOutList = Arrays.asList(bookingDtoOut);


        when(bookingService.findAllBookingsByOwner(userId, state, from, size)).thenReturn(bookingDtoOutList);

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", state)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(bookingDtoOut.getId()))
                .andExpect(jsonPath("$[0].status").value(bookingDtoOut.getStatus().toString()));
    }
}