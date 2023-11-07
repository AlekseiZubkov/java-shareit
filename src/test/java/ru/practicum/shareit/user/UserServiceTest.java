package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.user.dao.UserJpaRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exeption.EmailException;
import ru.practicum.shareit.user.exeption.UserIdException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
        when(userRepository.save(user1)).thenReturn(user1);
        userDto2 = userService.create(userDto1);

        assertEquals(userDto1, userDto2);
        verify(userRepository).save(user1);
    }

    @Test
    void getAll() {
        List<UserDto> expectedUsersDto = Arrays.asList(userDto1, new UserDto());
        List<User> actualUsers = Arrays.asList(user1, new User());
        when(userRepository.findAll()).thenReturn(actualUsers);

        List<UserDto> actualUsersDto = userService.getAll();

        assertEquals(expectedUsersDto, actualUsersDto);
        verify(userRepository).findAll();
    }

    @Test
    void getAllUsersEmpty() {
        List<UserDto> expectedUsers = userService.getAll();

        assertThat(expectedUsers, empty());
        verify(userRepository, times(1)).findAll();
    }


    @Test
    void updateUser() {
        when(userRepository.save(user1)).thenReturn(user1);
        userDto2 = userService.updateUser(userDto1, userId);

        assertEquals(userDto1, userDto2);
        verify(userRepository).save(user1);
    }

    @Test
    void updateUser_doublEmail_throwEmailException() {
        user2.setEmail(user1.getEmail());
        when(userRepository.findAll()).thenReturn(List.of(user2));
        assertThrows(EmailException.class, () -> userService.updateUser(userDto1, 1L));

    }

    @Test
    void deleteUser() {
        userService.deleteUser(userId);

        verify(userRepository).deleteById(userId);
    }

    @Test
    void getUser() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user1));

        userDto2 = userService.getUser(userId);

        assertEquals(userDto1, userDto2);
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void getUser_notFoundUser() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        assertThrows(UserIdException.class, () -> userService.getUser(userId));

    }

}