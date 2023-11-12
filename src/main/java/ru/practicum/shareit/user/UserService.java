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


    @Transactional(readOnly = true)
    public UserDto getUser(Long id) {
        log.info("Выполняется операция запроса пользователя");
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            return UserMapper.toUserDto(user.get());
        } else throw new UserIdException("Пользователь не найден");
    }

    @Transactional(readOnly = true)
    public List<UserDto> getAll() {
        log.info("Выполняется операция запроса пользователей");
        List<User> users = userRepository.findAll();
        List<UserDto> userDtos = new ArrayList<>();
        for (User user : users) {
            userDtos.add(UserMapper.toUserDto(user));
        }
        return userDtos;
    }

    @Transactional
    public UserDto create(UserDto user) {
        log.info("Выполняется операция создания пользователя");
        User newUser = UserMapper.toNewUser(user);
        return UserMapper.toUserDto(userRepository.save(newUser));
    }

    @Transactional
    public UserDto updateUser(UserDto userDto, Long id) {
        log.info("Выполняется операция обновления пользователя");
        Optional<User> userOptional = userRepository.findById(id);
        User user = UserMapper.toUser(userDto, id, userOptional);

        if (Objects.nonNull(userDto.getEmail())) {
            checkEmail(user);
        }
        return UserMapper.toUserDto(userRepository.save(user));
    }

    @Transactional
    public void deleteUser(long id) {
        log.info("Выполняется операция удаления пользователя");
        userRepository.deleteById(id);
    }


    private void checkEmail(User checkedUser) {
        List<User> users = userRepository.findAll();
        for (User user : users) {
            if (user.getEmail().equals(checkedUser.getEmail()) && !Objects.equals(user.getId(), checkedUser.getId())) {
                throw new EmailException("Email  повторяется");
            }
        }
    }


}
