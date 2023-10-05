package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UsersInMemoryStorageDao {
    List<UserDto> getAll();

    UserDto create(UserDto user);

    UserDto update(UserDto userDto, long id);

    void delete(long id);

    User getUserById(long id);
}
