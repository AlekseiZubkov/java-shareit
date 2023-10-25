package ru.practicum.shareit.booking.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

public interface BookingJpaRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBookerId(Long bookerId);

    List<Booking> findAllByItemOwnerId(Long userId);

    List<Booking> findByBookerIdOrderByStartDesc(Long bookerId);
    List<Booking> findByItem_Owner_IdOrderByStartDesc(Long ownerId);


}
