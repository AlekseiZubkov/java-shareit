package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dao.UserJpaRepository;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {
    @Mock
    private ItemJpaRepository itemRepository;
    @Mock
    private UserJpaRepository userRepository;
    @Mock
    private BookingJpaRepository bookingRepository;
    @Mock
    private BookingMapper bookingMapper;
    @InjectMocks
    private BookingService bookingService;

    Long bookerId;
    Long itemId;
    Long userId;
    Long bookingId;
    BookingDto bookingDto;
    BookingDtoOut outputDto;
    LocalDateTime current;
    private Item item;
    private User user;
    private Long requestId;
    private ItemRequest itemRequest;
    private Booking booking;

    @BeforeEach
    void setUp() {
        bookerId = 1L;
        itemId = 2L;
        userId = 3L;
        requestId = 4L;
        bookingId = 5L;
        current = LocalDateTime.now();

        bookingDto = new BookingDto();
        bookingDto.setItemId(itemId);
        bookingDto.setStart(current.plusDays(1));
        bookingDto.setEnd(current.plusDays(2));

        user = new User();
        user.setId(userId);
        user.setName("userName");
        user.setEmail("user@email.ru");

        itemRequest = new ItemRequest();
        itemRequest.setId(requestId);
        itemRequest.setDescription("request description");
        itemRequest.setRequester(user);
        itemRequest.setCreated(LocalDateTime.now());

        item = new Item();
        item.setId(itemId);
        item.setName("name");
        item.setDescription("description");
        item.setAvailable(true);
        item.setRequest(itemRequest);
        item.setOwner(user);

        booking = new Booking();
        booking.setId(bookingId);
        booking.setStart(current.plusDays(2));
        booking.setEnd(current.plusDays(5));
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStatus(Status.WAITING);

        outputDto = new BookingDtoOut();
        outputDto.setId(bookingId);
        outputDto.setStart(current.plusDays(2));
        outputDto.setEnd(current.plusDays(5));
        outputDto.setItem(item);
        outputDto.setBooker(user);
        outputDto.setStatus(Status.WAITING);


    }


    @Test
    void createBooking_ItemNotFound() {
        when(itemRepository.findById(bookingDto.getItemId())).thenReturn(Optional.empty());

        assertThrows(ItemIdException.class, () -> {
            bookingService.create(bookingDto, bookerId);
        });
    }

    @Test
    void createBooking_ValidationException() {

        when(itemRepository.findById(bookingDto.getItemId())).thenReturn(Optional.empty());

        assertThrows(ItemIdException.class, () -> {
            bookingService.create(bookingDto, bookerId);
        });
    }

    @Test
    void createBooking_InvalidAvailable_Exception() {
        Item item = new Item();
        item.setAvailable(false);

        when(itemRepository.findById(bookingDto.getItemId())).thenReturn(Optional.of(item));

        assertThrows(ValidationException.class, () -> {
            bookingService.create(bookingDto, bookerId);
        });
    }

    @Test
    void createBooking_InvalidBooker_Exception() {
        bookingDto.setEnd(current.plusDays(3));
        bookingDto.setStart(current.plusDays(2));

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        assertThrows(BookingException.class, () -> {
            bookingService.create(bookingDto, userId);
        });
    }

    @Test
    void findAllBookingsByBooker_InvalidState_IllegalArgumentException() {
        String stateStr = "Unknown";
        assertThrows(StateException.class, () -> {
            bookingService.findAllBookingsByBooker(userId, stateStr, 0L, 5L);
        });
    }

    @Test
    void findAllBookingsByOwner_InvalidState_IllegalArgumentException() {
        String stateStr = "Unknown";
        assertThrows(StateException.class, () -> {
            bookingService.findAllBookingsByOwner(userId, stateStr, 0L, 5L);
        });
    }

    @Test
    void createBooking() {
        Long bookerId = 6L;
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(userRepository.findById(bookerId)).thenReturn(Optional.of(user));

        BookingDtoOut bookingDtoOut = bookingService.create(bookingDto, bookerId);
        assertEquals(bookingDtoOut.getStart(), bookingDto.getStart());
        assertEquals(bookingDtoOut.getEnd(), bookingDto.getEnd());
    }

    @Test
    void createBooking_InvalidTimeRange() {
        bookingDto.setStart(LocalDateTime.now());
        bookingDto.setEnd(LocalDateTime.now().minusHours(1));

        assertThrows(ItemIdException.class, () -> {
            bookingService.create(bookingDto, bookerId);
        });
    }

    @Test
    void createBooking_NotAllowedOwner() {
        Item item = new Item();
        item.setOwner(new User());
        item.getOwner().setId(bookerId);

        when(itemRepository.findById(bookingDto.getItemId())).thenReturn(Optional.of(item));

        assertThrows(NullPointerException.class, () -> {
            bookingService.create(bookingDto, bookerId);
        });
    }

    @Test
    void updateBooking_whenBookingNotFound_thenBookingExceptionThrown() {
        Boolean approved = true;
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        assertThrows(BookingException.class,
                () -> bookingService.updateBooking(userId, bookingId, approved));

        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void updateBooking_whenBookingRejected_thenValidationExceptionThrown() {
        Boolean approved = true;
        booking.setStatus(Status.REJECTED);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        assertThrows(ValidationException.class,
                () -> bookingService.updateBooking(userId, bookingId, approved));

        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void updateBooking_whenUserIsNotItemOwner_thenBookingNotOwnerExceptionThrown() {
        Boolean approved = true;
        User newUser = new User();
        Item newItem = Item.builder()
                .id(itemId)
                .name("name")
                .description("description")
                .available(true)
                .request(itemRequest)
                .owner(newUser)
                .build();
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(itemRepository.findAllByOwner_Id(userId)).thenReturn(List.of(newItem));


        assertThrows(BookingNotOwnerException.class,
                () -> bookingService.updateBooking(userId, bookingId, approved));

        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void updateBooking_whenStatusRejected_thenBookingRejectedAndReturnDto() {
        Boolean approved = false;
        outputDto.setStatus(Status.REJECTED);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(itemRepository.findAllByOwner_Id(userId)).thenReturn(List.of(item));
        when(bookingRepository.save(booking)).thenReturn(booking);

        BookingDtoOut actualDto = bookingService.updateBooking(userId, bookingId, approved);

        assertEquals(outputDto, actualDto);
    }

    @Test
    void updateBooking_whenStatusApproved_thenBookingApprovedAndReturnDto() {
        Boolean approved = true;
        outputDto.setStatus(Status.APPROVED);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(itemRepository.findAllByOwner_Id(userId)).thenReturn(List.of(item));
        when(bookingRepository.save(booking)).thenReturn(booking);

        BookingDtoOut actualDto = bookingService.updateBooking(userId, bookingId, approved);

        assertEquals(outputDto, actualDto);
    }

    @Test
    void findBooking_whenBookingNotFound_thenNotFoundBookingExceptionThrown() {
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        assertThrows(BookingException.class,
                () -> bookingService.findBooking(userId, bookingId));

    }

    @Test
    void findBooking_whenUserIsNotItemOwnerOrBooker_thenAuthOwnerExceptionThrown() {
        User newUser = new User();
        userId = 100L;
        Item newItem = Item.builder()
                .id(itemId)
                .name("name")
                .description("description")
                .available(true)
                .request(itemRequest)
                .owner(newUser)
                .build();
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(itemRepository.findAllByOwner_Id(userId)).thenReturn(List.of(newItem));

        assertThrows(BookingNotOwnerException.class,
                () -> bookingService.findBooking(userId, bookingId));
    }

    @Test
    void findBooking_whenBookingFound_thenReturnDto() {
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(itemRepository.findAllByOwner_Id(userId)).thenReturn(List.of(item));

        BookingDtoOut actualDto = bookingService.findBooking(userId, bookingId);

        assertEquals(outputDto, actualDto);
    }


    @Test
    void findAllBookingsByBooker_whenStateAll_thenReturnAllBookings() {
        String searchedState = "ALL";
        PageRequest pageRequest = PageRequest.of((int) (0L / 3L), 3, Sort.Direction.DESC, "start");
        List<Booking> bookings = new ArrayList<>(List.of(booking));
        List<BookingDtoOut> expectedBookingsDto = new ArrayList<>(List.of(outputDto));
        when(bookingRepository.findByBookerId(userId, pageRequest))
                .thenReturn(bookings);

        List<BookingDtoOut> actualBookingsDto
                = bookingService.findAllBookingsByBooker(userId, searchedState, 0L, 3L);

        assertEquals(expectedBookingsDto, actualBookingsDto);
    }

    @Test
    void findAllBookingsByBooker_whenStateWaiting_thenReturnWaitingBookings() {
        String searchedState = "WAITING";
        PageRequest pageRequest = PageRequest.of((int) (0L / 3L), 3, Sort.Direction.DESC, "start");
        List<Booking> bookings = new ArrayList<>(List.of(booking));
        List<BookingDtoOut> expectedBookingsDto = new ArrayList<>(List.of(outputDto));
        when(bookingRepository.findByBookerId(userId, pageRequest))
                .thenReturn(bookings);

        List<BookingDtoOut> actualBookingsDto
                = bookingService.findAllBookingsByBooker(userId, searchedState, 0L, 3L);

        assertEquals(expectedBookingsDto, actualBookingsDto);
    }

    @Test
    void findAllBookingsByBooker_whenStateCurrent_thenReturnCurrentBookings() {
        String searchedState = "CURRENT";
        PageRequest pageRequest = PageRequest.of((int) (0L / 3L), 3, Sort.Direction.DESC, "start");
        booking.setStart(current.minusDays(1));
        booking.setEnd(current.plusDays(1));
        outputDto.setStart(current.minusDays(1));
        outputDto.setEnd(current.plusDays(1));
        List<Booking> bookings = new ArrayList<>(List.of(booking));
        List<BookingDtoOut> expectedBookingsDto = new ArrayList<>(List.of(outputDto));
        when(bookingRepository.findByBookerId(userId, pageRequest)).thenReturn(bookings);

        List<BookingDtoOut> actualBookingsDto
                = bookingService.findAllBookingsByBooker(userId, searchedState, 0L, 3L);

        assertEquals(expectedBookingsDto, actualBookingsDto);
    }

    @Test
    void findAllBookingsByBooker_whenStateFuture_thenReturnFutureBookings() {
        String searchedState = "FUTURE";
        PageRequest pageRequest = PageRequest.of((int) (0L / 3L), 3, Sort.Direction.DESC, "start");
        booking.setStart(current.plusDays(1));
        booking.setEnd(current.plusDays(2));
        outputDto.setStart(current.plusDays(1));
        outputDto.setEnd(current.plusDays(2));
        List<Booking> bookings = new ArrayList<>(List.of(booking));
        List<BookingDtoOut> expectedBookingsDto = new ArrayList<>(List.of(outputDto));
        when(bookingRepository.findByBookerId(userId, pageRequest)).thenReturn(bookings);

        List<BookingDtoOut> actualBookingsDto
                = bookingService.findAllBookingsByBooker(userId, searchedState, 0L, 3L);

        assertEquals(expectedBookingsDto, actualBookingsDto);
    }

    @Test
    void findAllBookingsByBooker_whenStatePast_thenReturnPastBookings() {
        String searchedState = "PAST";
        PageRequest pageRequest = PageRequest.of((int) (0L / 3L), 3, Sort.Direction.DESC, "start");
        booking.setStart(current.minusDays(2));
        booking.setEnd(current.minusDays(1));
        outputDto.setStart(current.minusDays(2));
        outputDto.setEnd(current.minusDays(1));
        booking.setStatus(Status.APPROVED);
        outputDto.setStatus(Status.APPROVED);
        List<Booking> bookings = new ArrayList<>(List.of(booking));
        List<BookingDtoOut> expectedBookingsDto = new ArrayList<>(List.of(outputDto));
        when(bookingRepository.findByBookerId(userId, pageRequest)).thenReturn(bookings);

        List<BookingDtoOut> actualBookingsDto
                = bookingService.findAllBookingsByBooker(userId, searchedState, 0L, 3L);

        assertEquals(expectedBookingsDto, actualBookingsDto);
    }

    @Test
    void findAllBookingsByBookerId_whenStateRejected_thenReturnRejectedBookings() {
        String searchedState = "REJECTED";
        PageRequest pageRequest = PageRequest.of((int) (0L / 3L), 3, Sort.Direction.DESC, "start");
        booking.setStatus(Status.CANCELED);
        outputDto.setStatus(Status.CANCELED);
        List<Booking> bookings = new ArrayList<>(List.of(booking));
        List<BookingDtoOut> expectedBookingsDto = new ArrayList<>(List.of(outputDto));
        when(bookingRepository.findByBookerId(userId, pageRequest)).thenReturn(bookings);

        List<BookingDtoOut> actualBookingsDto
                = bookingService.findAllBookingsByBooker(userId, searchedState, 0L, 3L);

        assertEquals(expectedBookingsDto, actualBookingsDto);
    }

    @Test
    void findAllBookingsByOwnerId_whenBookingsNotFound_thenNotFoundBookingExceptionThrown() {
        String searchedState = "ALL";
        PageRequest pageRequest = PageRequest.of((int) (0L / 3L), 3);
        when(bookingRepository.findByItem_Owner_IdOrderByStartDesc(userId, pageRequest)).thenReturn(new ArrayList<>());

        assertThrows(BookingException.class,
                () -> bookingService.findAllBookingsByOwner(userId, searchedState, 0L, 3L));
    }

    @Test
    void findAllBookingsByOwnerId_whenStateAll_thenReturnAllBookings() {
        String searchedState = "ALL";
        PageRequest pageRequest = PageRequest.of((int) (0L / 3L), 3);
        List<Booking> bookings = new ArrayList<>(List.of(booking));
        List<BookingDtoOut> expectedBookingsDto = new ArrayList<>(List.of(outputDto));
        when(bookingRepository.findByItem_Owner_IdOrderByStartDesc(userId, pageRequest)).thenReturn(bookings);

        List<BookingDtoOut> actualBookingsDto
                = bookingService.findAllBookingsByOwner(userId, searchedState, 0L, 3L);

        assertEquals(expectedBookingsDto, actualBookingsDto);
    }

}