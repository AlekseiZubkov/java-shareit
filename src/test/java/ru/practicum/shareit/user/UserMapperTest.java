
package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.user.dao.UserJpaRepository;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@SpringBootTest
class UserMapperTest {

    @Mock
    private UserJpaRepository userRepository;

    @InjectMocks
    private UserMapper userMapper;

    private User user;
    private UserDto userDto;
    private Long userId = 1L;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(userId)
                .name("Alex")
                .email("alex@mail.ru")
                .build();
        userDto = new UserDto(userId, "Alex", "alex@mail.ru");
    }

    @Test
    void toUserDto() {
        UserDto mappedDto = userMapper.toUserDto(user);
        assertEquals(userDto, mappedDto);
    }

    @Test
    void toUser_withExistingUser() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        User mappedUser = userMapper.toUser(userDto, userId);
        assertEquals(user, mappedUser);
    }

    @Test
    void toUser_withNewUser() {
        User newuser = userMapper.toNewUser(userDto);

        User mappedUser = userMapper.toUser(userDto, userId);
        assertEquals(newuser, mappedUser);
    }

    @Test
    void toNewUser() {
        User newUser = userMapper.toNewUser(userDto);
        assertEquals(user, newUser);
    }
}