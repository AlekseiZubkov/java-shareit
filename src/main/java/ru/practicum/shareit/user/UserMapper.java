package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.dao.JpaRepositoryUser;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Objects;
import java.util.Optional;

@Component
@AllArgsConstructor
public class UserMapper {
    private final JpaRepositoryUser userRepository;
    public UserDto toUserDto(User user) {
        return new UserDto(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }

    public User toUser(UserDto userDto,Long id) {
        Optional<User> userOptional = userRepository.findById(id);
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
    public User toNewUser(UserDto dto) {
        return new User(
                dto.getId(),
                dto.getName(),
                dto.getEmail()
        );
    }
}
