package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.UserDto;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {
    private final UserMapper userMapper ;
    private User user1;
    private User user2;
    private UserDto userDto1;

    private UserDto userDto2;

    UserMapperTest(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @BeforeEach
    void setUp() {
        user1 = new User(1L, "name User1", "user1@emal.ru");
        user2 = new User();
        userDto1 = new UserDto(1L, "name User1", "user1@emal.ru");
        userDto2 = new UserDto();
    }

    @Test
    void toUserDto() {
        userDto2 = userMapper.toUserDto(user1);
        assertEquals(userDto1.getId(), userDto2.getId());
    }

    @Test
    void toUser() {
    }

    @Test
    void toNewUser() {
    }
}