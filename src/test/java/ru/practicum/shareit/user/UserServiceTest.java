package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.user.dao.UserJpaRepository;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserJpaRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @InjectMocks
    private UserService userService;
    private Long userId = 1L;
    private User user1;
    private User user2;
    private UserDto userDto1;

    private UserDto userDto2;

    @BeforeEach
    void setUp() {
        user1 = new User(1L, "name User1", "user1@emal.ru");
        user2 = new User();
        userDto1 = new UserDto(1L, "name User1", "user1@emal.ru");
        userDto2 = new UserDto();
    }

    @Test
    void createUser() {
        when(userMapper.toNewUser(userDto1)).thenReturn(user1);
        when(userRepository.save(user1)).thenReturn(user1);
        when(userMapper.toUserDto(user1)).thenReturn(userDto1);

        userDto2 = userService.create(userDto1);

        assertEquals(userDto1, userDto2);
        verify(userRepository).save(user1);
    }

    @Test
    void getAll() {
        List<UserDto> expectedUsersDto = Arrays.asList(userDto1, new UserDto());
        List<User> actualUsers = Arrays.asList(user1, new User());
        when(userRepository.findAll()).thenReturn(actualUsers);
        when(userMapper.toUserDto(user1)).thenReturn(userDto1);
        when(userMapper.toUserDto(user2)).thenReturn(userDto2);

        List<UserDto> actualUsersDto = userService.getAll();

        assertEquals(expectedUsersDto, actualUsersDto);
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void getAllUsersEmpty() {
        List<UserDto> expectedUsers = userService.getAll();

        assertThat(expectedUsers, empty());
        verify(userRepository, times(1)).findAll();
    }


    @Test
    void updateUser() {
        when(userMapper.toUser(userDto1, user1.getId())).thenReturn(user1);
        when(userRepository.save(user1)).thenReturn(user1);
        when(userMapper.toUserDto(user1)).thenReturn(userDto1);

        userDto2 = userService.updateUser(userDto1, userId);

        assertEquals(userDto1, userDto2);
        verify(userRepository).save(user1);
    }

    @Test
    void deleteUser() {
        userService.deleteUser(userId);

        verify(userRepository).deleteById(userId);
    }

    @Test
    void getUser() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user1));
        when(userMapper.toUserDto(user1)).thenReturn(userDto1);

        userDto2 = userService.getUser(userId);

        assertEquals(userDto1, userDto2);
        verify(userRepository, times(1)).findById(userId);
    }

}