package ru.practicum.shareit.booking.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingJpaRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBookerId(Long bookerId);

    List<Booking> findAllByItemOwnerId(Long userId);

    List<Booking> findByBookerIdOrderByStartDesc(Long bookerId);
    List<Booking> findByItem_Owner_IdOrderByStartDesc(Long ownerId);
    List<Booking> findByItem_IdAndEndBeforeAndStatusAndBooker_Id(Long itemId, LocalDateTime end, Status status, Long bookerId);
    List<Booking> findFirstByItem_IdAndStatusAndEndBeforeOrderByEndDesc(Long id, Status status, LocalDateTime time);
    List<Booking> findFirstByItem_IdAndStatusAndEndAfterOrderByStartAsc(Long id, Status status, LocalDateTime time);

}
