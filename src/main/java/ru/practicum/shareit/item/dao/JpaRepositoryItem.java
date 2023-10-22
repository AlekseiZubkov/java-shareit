package ru.practicum.shareit.item.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.util.List;

public interface JpaRepositoryItem extends JpaRepository<Item, Long> {
    List<Item> findAllByOwner_IdOrderById(Long owner);
}
