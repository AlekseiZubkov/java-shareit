package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dao.UserJpaRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exeption.EmailException;
import ru.practicum.shareit.user.exeption.UserIdException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
public class UserService {
    private final UserJpaRepository userRepository;
    private final UserMapper userMapper;

    @Transactional(readOnly = true)
    public UserDto getUser(Long id) {
        log.info("Выполняется операция запроса пользователя");
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            return userMapper.toUserDto(user.get());
        } else throw new UserIdException("Пользователь не найден");
    }

    @Transactional(readOnly = true)
    public List<UserDto> getAll() {
        log.info("Выполняется операция запроса пользователей");
        List<User> users = userRepository.findAll();
        List<UserDto> userDtos = new ArrayList<>();
        for (User user : users) {
            userDtos.add(userMapper.toUserDto(user));
        }
        return userDtos;
    }

    @Transactional
    public UserDto create(UserDto user) {
        log.info("Выполняется операция создания пользователя");
        User newUser = userMapper.toNewUser(user);
        return userMapper.toUserDto(userRepository.save(newUser));
    }

    @Transactional
    public UserDto updateUser(UserDto userDto, Long id) {
        log.info("Выполняется операция обновления пользователя");
        User user = userMapper.toUser(userDto, id);
        if (Objects.nonNull(userDto.getEmail())) {
            System.out.println("nen");
            checkEmail(user);
        }
        return userMapper.toUserDto(userRepository.save(user));
    }

    @Transactional
    public void deleteUser(long id) {
        log.info("Выполняется операция удаления пользователя");
        userRepository.deleteById(id);
    }


    private void checkEmail(User checkedUser) {
        List<User> users = userRepository.findAll();
        for (User user : users) {
            System.out.println("checkedUser" + checkedUser);
            System.out.println("user1" + user);
            if (user.getEmail().equals(checkedUser.getEmail()) && !Objects.equals(user.getId(), checkedUser.getId())) {
                throw new EmailException("Email  повторяется");
            }
        }
    }


}
