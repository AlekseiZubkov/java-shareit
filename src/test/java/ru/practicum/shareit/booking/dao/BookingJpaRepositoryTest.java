package ru.practicum.shareit.booking.dao;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.dao.ItemJpaRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dao.UserJpaRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class BookingJpaRepositoryTest {

    @Autowired
    private BookingJpaRepository bookingRepository;
    @Autowired
    private UserJpaRepository userRepository;
    @Autowired
    private ItemJpaRepository itemJpaRepository;
    private User booker;
    private User owner;
    private Item item;

    @BeforeEach
    public void setUp() {
        booker = userRepository.save(User.builder()
                .name("Booker")
                .email("booker@mail.com")
                .build());

        owner = userRepository.save(User.builder()
                .name("Owner")
                .email("owner@mail.com")
                .build());

        item = Item.builder()
                .name("Item 1")
                .description("Description 1")
                .available(true)
                .owner(owner)
                .build();
        item = itemJpaRepository.save(item);
    }

    @AfterEach
    public void tearDown() {
        bookingRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void findByBookerId_whenValidBookerId_returnBookings() {
        Booking booking1 = createBooking(booker, item, LocalDateTime.now(), LocalDateTime.now().plusHours(1));
        Booking booking2 = createBooking(booker, item, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(1).plusHours(1));

        List<Booking> bookings = bookingRepository.findByBookerId(booker.getId(), PageRequest.of(0, 10));

        assertEquals(2, bookings.size());
        assertTrue(bookings.contains(booking1));
        assertTrue(bookings.contains(booking2));
    }

    @Test
    void findByItem_Owner_IdOrderByStartDesc_whenValidOwnerId_returnBookings() {
        Booking booking1 = createBooking(booker, item, LocalDateTime.now(), LocalDateTime.now().plusHours(1));
        Booking booking2 = createBooking(booker, item, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(1).plusHours(1));

        List<Booking> bookings = bookingRepository.findByItem_Owner_IdOrderByStartDesc(owner.getId(), PageRequest.of(0, 10));

        assertEquals(2, bookings.size());
        assertTrue(bookings.contains(booking1));
        assertTrue(bookings.contains(booking2));
    }

    @Test
    void existsByItem_IdAndEndBeforeAndStatusAndBooker_Id_whenValidParameters_returnTrue() {
        Booking booking = createBooking(booker, item, LocalDateTime.now().minusDays(1), LocalDateTime.now().minusDays(1).plusHours(1));

        boolean exists = bookingRepository.existsByItem_IdAndEndBeforeAndStatusAndBooker_Id(
                item.getId(), LocalDateTime.now(), Status.APPROVED, booker.getId());

        assertTrue(exists);
    }

    @Test
    void existsByItem_IdAndEndBeforeAndStatusAndBooker_Id_whenInvalidParameters_returnFalse() {
        Booking booking = createBooking(booker, item, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(1).plusHours(1));

        boolean exists = bookingRepository.existsByItem_IdAndEndBeforeAndStatusAndBooker_Id(
                item.getId(), LocalDateTime.now(), Status.APPROVED, booker.getId());

        assertFalse(exists);
    }


    private Booking createBooking(User booker, Item item, LocalDateTime start, LocalDateTime end) {
        Booking booking = Booking.builder()
                .start(start)
                .end(end)
                .item(item)
                .booker(booker)
                .status(Status.APPROVED)
                .build();
        return bookingRepository.save(booking);
    }
}