package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dao.UsersInMemoryStorageDao;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class UserService {
    private final UsersInMemoryStorageDao userStorage;

    private final UserMapper userMapper;

    public UserDto getUser(long id) {
        log.info("Выполняется операция запроса пользователя");

        return userMapper.toUserDto(userStorage.getUserById(id));
    }

    public List<UserDto> getAll() {
        log.info("Выполняется операция запроса пользователей");
        return userStorage.getAll();
    }

    public UserDto create(UserDto user) {
        log.info("Выполняется операция создания пользователя");
        return userStorage.create(user);
    }

    public UserDto updateUser(UserDto userDto, Long id) {
        log.info("Выполняется операция обновления пользователя");
        return userStorage.update(userDto, id);
    }

    public void deleteUser(long id) {
        log.info("Выполняется операция удаления пользователя");
        userStorage.delete(id);
    }

}
