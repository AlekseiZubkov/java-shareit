package ru.practicum.shareit.user.dao;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.user.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class UserJpaRepositoryTest {

    @Autowired
    private UserJpaRepository userRepository;

    @BeforeEach
    public void addRequest() {
        userRepository.save(User.builder()
                .name("Name")
                .email("name@mail.ru")
                .build());

    }

    @AfterEach
    public void deleteRequest() {
        userRepository.deleteAll();
    }

    @Test
    void findAll_whenInvoke_listUsersReturn() {
        List<User> users = userRepository.findAll();

        assertEquals(1, users.size());
        assertEquals("Name", users.get(0).getName());
    }
}