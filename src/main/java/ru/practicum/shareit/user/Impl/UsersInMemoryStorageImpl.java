package ru.practicum.shareit.user.Impl;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dao.UsersInMemoryStorageDao;
import ru.practicum.shareit.user.exeption.EmailException;
import ru.practicum.shareit.user.exeption.UserIdException;

import javax.validation.ValidationException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
@NoArgsConstructor
@AllArgsConstructor

public class UsersInMemoryStorageImpl implements UsersInMemoryStorageDao {
    private final Map<Long, User> users = new HashMap<>();
    private long id = 1;

    @Override
    public User create(User user) {
        if (!checkEmail(user)) {
            user.setId(id++);
            users.put(user.getId(), user);
            System.out.println("Users" + users);
        }
        return user;
    }

    @Override
    public User update(User user, long id) {
        if (!users.containsKey(id)) {
            throw new UserIdException("Id не найден");
        }
        User updateUser = users.get(id);
        if (user.getName() != null) {
            updateUser.setName(user.getName());
        }
        if (user.getEmail() != null && !updateUser.getEmail().equals(user.getEmail())) {
            checkEmail(user);
            updateUser.setEmail(user.getEmail());
        }
        return updateUser;
    }

    @Override
    public void delete(long id) {
        users.remove(id);
    }

    @Override
    public List<User> getAll() {

        return new ArrayList<>(users.values());
    }

    @Override
    public User getUserById(long id) {

        return users.get(id);
    }

    private boolean checkEmail(User user) {
        if (user.getEmail() == null) {
            throw new ValidationException("email Не может быть null");
        }
        for (User user2 : users.values()) {
            if (user2.getEmail().equals(user.getEmail())) {
                throw new EmailException("email уже занят");
            }
        }
        return false;
    }

}
