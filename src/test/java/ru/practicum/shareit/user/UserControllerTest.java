package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    UserService userService;
    UserDto userDto = UserDto.builder()
            .id(1L)
            .name("John Doe")
            .email("john@mail.ru")
            .build();

    UserDto savedUserDto = UserDto.builder()
            .id(1L)
            .name("John Doe")
            .email("john@mail.ru")
            .build();

    @SneakyThrows
    @Test
    void getAll() {
        List<UserDto> users = Arrays.asList(new UserDto(), new UserDto());
        when(userService.getAll()).thenReturn(users);

        mockMvc.perform(get("/users"))
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()", is(users.size())));
    }

    @SneakyThrows
    @Test
    void getUser() {
        long userId = 1L;
        mockMvc.perform(get("/users/{id}", userId));
        //.andDo(print());
        verify(userService).getUser(userId);
    }

    @SneakyThrows
    @Test
    void create() {

        when(userService.create(any(UserDto.class))).thenReturn(savedUserDto);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8.name()))
                //.andDo(print());
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("John Doe")))
                .andExpect(jsonPath("$.email", is("john@mail.ru")));

        verify(userService).create(userDto);

    }

    @SneakyThrows
    @Test
    void update() {
        long userId = 1L;
        UserDto updatedUserDto = UserDto.builder()
                .id(userId)
                .name("Updated Name")
                .email("updated@mail.ru")
                .build();

        when(userService.updateUser(any(), any(Long.class)))
                .thenReturn(userDto);

        mockMvc.perform(MockMvcRequestBuilders.patch("/users/1")
                        .content(objectMapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("John Doe")))
                .andExpect(jsonPath("$.email", is("john@mail.ru")));

        verify(userService).updateUser(userDto, userId);
    }


}