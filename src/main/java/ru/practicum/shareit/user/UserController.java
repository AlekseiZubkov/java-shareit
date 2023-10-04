package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@Slf4j
@AllArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/users")
    public List<User> getAll() {
        log.info("Пришел Get запрос всех пользователей");
        return userService.getAll();
    }

    @GetMapping("/users/{id}")
    public User getUser(@PathVariable long id) {
        log.info("Пришел Get запрос пользователя с id {}", id);
        return userService.getUser(id);
    }

    @PostMapping(value = "/users")
    public User create(@Valid @RequestBody User user) {
        log.info("Пришел Post запрос пользователя");
        return userService.create(user);
    }

    @PatchMapping("/users/{id}")
    public User updateUser(@Valid @RequestBody User user, @PathVariable Long id) {
        log.info("Пришел Patch запрос обновления пользователя");
        return userService.updateUser(user, id);
    }

    @DeleteMapping("/users/{id}")
    public void deleteUser(@PathVariable long id) {
        log.info("Пришел DELETE запрос пользователя с id {}", id);
        userService.deleteUser(id);
    }
}
