package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.user.User;

import java.util.List;

public interface UsersInMemoryStorageDao {
    List<User> getAll();

    User create(User user);

    User update(User user, long id);

    void delete(long id);

    User getUserById(long id);
}
