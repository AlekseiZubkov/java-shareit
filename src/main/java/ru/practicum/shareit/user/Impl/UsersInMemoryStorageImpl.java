package ru.practicum.shareit.user.Impl;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dao.UsersInMemoryStorageDao;
import ru.practicum.shareit.user.dto.UserDto;
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
    private final UserMapper userMapper = new UserMapper();
    private long id = 1;

    @Override
    public UserDto create(UserDto userDto) {
        User user = userMapper.toUser(userDto);
        if (!checkEmail(user)) {
            user.setId(id++);
            users.put(user.getId(), user);
            System.out.println("Users" + users);
        }
        return userMapper.toUserDto(user);
    }

    @Override
    public UserDto update(UserDto userDto, long id) {
        if (!users.containsKey(id)) {
            throw new UserIdException("Id не найден");
        }
        User user = userMapper.toUser(userDto);
        User updateUser = users.get(id);
        if (user.getName() != null) {
            updateUser.setName(user.getName());
        }
        if (user.getEmail() != null && !updateUser.getEmail().equals(user.getEmail())) {
            checkEmail(user);
            updateUser.setEmail(user.getEmail());
        }
        return userMapper.toUserDto(updateUser);
    }

    @Override
    public void delete(long id) {
        users.remove(id);
    }

    @Override
    public List<UserDto> getAll() {
        List<UserDto> UsersDto = new ArrayList<>();
        for (User user : users.values()) {
            UsersDto.add(userMapper.toUserDto(user));
        }

        return UsersDto;
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
