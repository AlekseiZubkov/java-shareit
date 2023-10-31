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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
            .name("Alex")
            .email("Alex@mail.ru")
            .build();

    UserDto savedUserDto = UserDto.builder()
            .id(1L)
            .name("Alex")
            .email("Alex@mail.ru")
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
    void deleteUser() {
        long userId = 1L;
        mockMvc.perform(delete("/users/{id}", userId));
        //.andDo(print());
        verify(userService).deleteUser(userId);
    }

    @SneakyThrows
    @Test
    void create() {

        when(userService.create(any(UserDto.class))).thenReturn(savedUserDto);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8.name()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Alex")))
                .andExpect(jsonPath("$.email", is("Alex@mail.ru")));

        verify(userService).create(userDto);

    }

    @SneakyThrows
    @Test
    void update() {
        long userId = 1L;
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
                .andExpect(jsonPath("$.name", is("Alex")))
                .andExpect(jsonPath("$.email", is("Alex@mail.ru")));

        verify(userService).updateUser(userDto, userId);
    }


}