package ru.practicum.shareit.booking.dao;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingJpaRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBookerId(Long bookerId);

    List<Booking> findAllByItemOwnerId(Long userId);

    List<Booking> findByBookerId(Long bookerId, PageRequest pageRequest);

    List<Booking> findByItem_Owner_IdOrderByStartDesc(Long ownerId,PageRequest pageRequest);

    Boolean existsByItem_IdAndEndBeforeAndStatusAndBooker_Id(Long itemId, LocalDateTime end, Status status, Long bookerId);

    List<Booking> findFirstByItem_IdAndStatusAndStartAfterOrderByStartAsc(Long id, Status status, LocalDateTime time);

    List<Booking> findFirstByItem_IdAndStatusAndStartBeforeOrderByStartDesc(Long id, Status status, LocalDateTime time);

}
