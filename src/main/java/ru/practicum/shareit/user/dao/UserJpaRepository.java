package ru.practicum.shareit.user.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.user.User;

public interface UserJpaRepository extends JpaRepository<User, Long> {

}
