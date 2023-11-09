
package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.user.dao.UserJpaRepository;
import ru.practicum.shareit.user.dto.UserDto;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
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
    void toNewUser() {
        User newUser = userMapper.toNewUser(userDto);
        assertEquals(user, newUser);
    }
}