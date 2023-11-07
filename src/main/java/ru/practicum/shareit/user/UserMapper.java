package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import ru.practicum.shareit.user.dao.UserJpaRepository;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Objects;
import java.util.Optional;


@AllArgsConstructor

public class UserMapper {
    private static  UserJpaRepository userRepository;

    public static UserDto toUserDto(User user) {
        return new UserDto(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }

    public static User toUser(UserDto userDto, Long id, Optional<User> userOptional) {
        User user = new User();
        if (userOptional.isPresent()) {
            user = userOptional.get();
        }
        String name = user.getName();
        String email = user.getEmail();
        if (Objects.nonNull(userDto.getName())) {
            name = userDto.getName();
        }
        if (Objects.nonNull(userDto.getEmail())) {
            email = userDto.getEmail();
        }
        return new User(
                id,
                name,
                email
        );

    }

    public static User toNewUser(UserDto dto) {
        return new User(
                dto.getId(),
                dto.getName(),
                dto.getEmail()
        );
    }
}
