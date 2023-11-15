package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;

    @GetMapping
    public List<UserDto> getAll() {
        log.info("Пришел Get запрос всех пользователей");
        return userService.getAll();

    }

    @GetMapping("/{id}")
    public UserDto getUser(@PathVariable long id) {
        log.info("Пришел Get запрос пользователя с id {}", id);
        return userService.getUser(id);
    }

    @PostMapping
    public UserDto create(@Valid @RequestBody UserDto user) {
        log.info("Пришел Post запрос пользователя");
        return userService.create(user);
    }

    @PatchMapping("/{id}")
    public UserDto update(@Valid @RequestBody UserDto user, @PathVariable Long id) {
        log.info("Пришел Patch запрос обновления пользователя");
        return userService.updateUser(user, id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id) {
        log.info("Пришел DELETE запрос пользователя с id {}", id);
        userService.deleteUser(id);
    }
}
