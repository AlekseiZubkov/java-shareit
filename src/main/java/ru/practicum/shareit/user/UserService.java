package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dao.UsersInMemoryStorageDao;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.ArrayList;
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
        List<User> users = userStorage.getAll();
        List<UserDto> userDtos = new ArrayList<>();
        for (User user : users) {
            userDtos.add(userMapper.toUserDto(user));
        }
        return userDtos;
    }

    public UserDto create(UserDto user) {
        log.info("Выполняется операция создания пользователя");
        return userMapper.toUserDto(userStorage.create(userMapper.toUser(user)));
    }

    public UserDto updateUser(UserDto userDto, Long id) {
        log.info("Выполняется операция обновления пользователя");
        return userMapper.toUserDto(userStorage.update(userMapper.toUser(userDto), id));
    }

    public void deleteUser(long id) {
        log.info("Выполняется операция удаления пользователя");
        userStorage.delete(id);
    }

}
