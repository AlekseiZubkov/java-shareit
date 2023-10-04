package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dao.UsersInMemoryStorageDao;

import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class UserService {
    private final UsersInMemoryStorageDao userStorage;

    public User getUser(long id) {
        log.info("Выполняется операция запроса пользователя");

        return userStorage.getUserById(id);
    }

    public List<User> getAll() {
        log.info("Выполняется операция запроса пользователей");
        return userStorage.getAll();
    }

    public User create(User user) {
        log.info("Выполняется операция создания пользователя");
        return userStorage.create(user);
    }

    public User updateUser(User user, Long id) {
        log.info("Выполняется операция обновления пользователя");
        return userStorage.update(user, id);
    }

    public void deleteUser(long id) {
        log.info("Выполняется операция удаления пользователя");
        userStorage.delete(id);
    }

}
