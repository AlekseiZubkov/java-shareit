package ru.practicum.shareit.item.dao;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dao.UserJpaRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class ItemJpaRepositoryTest {

    @Autowired
    private ItemJpaRepository itemRepository;
    @Autowired
    private UserJpaRepository userJpaRepository;
    @Autowired
    private ItemRequestRepository itemRequestRepository;
    private User user;
    private Item item;

    @BeforeEach
    public void setup() {

        user = User.builder()
                .name("NameUser")
                .email("user@mail.ru")
                .build();

        item = Item.builder()
                .name("Name")
                .description("Description")
                .available(true)
                .owner(user)
                .build();

        user = userJpaRepository.save(user);
        item = itemRepository.save(item);
    }

    @AfterEach
    public void cleanup() {

        itemRepository.deleteAll();
        userJpaRepository.deleteAll();
    }

    @Test
    void findAllByOwner_IdOrderById_whenInvoke_returnItemsByOwner() {
        List<Item> items = itemRepository.findAllByOwner_IdOrderById(user.getId());

        assertEquals(1, items.size());
        assertEquals("Name", items.get(0).getName());
        assertEquals("Description", items.get(0).getDescription());
        assertEquals(user.getId(), items.get(0).getOwner().getId());
    }

    @Test
    void findAllByOwner_Id_whenInvoke_returnItemsByOwner() {
        List<Item> items = itemRepository.findAllByOwner_Id(user.getId());

        assertEquals(1, items.size());
        assertEquals("Name", items.get(0).getName());
        assertEquals("Description", items.get(0).getDescription());
        assertEquals(user.getId(), items.get(0).getOwner().getId());
    }

    @Test
    void findAllByRequest_id_whenInvoke_returnItemsByRequestId() {

        ItemRequest itemRequest = ItemRequest.builder()
                .description("Item request")
                .requester(user)
                .created(LocalDateTime.now())
                .build();
        item.setRequest(itemRequest);
        itemRequest = itemRequestRepository.save(itemRequest);
        item = itemRepository.save(item);

        List<Item> items = itemRepository.findAllByRequest_id(itemRequest.getId());

        assertEquals(1, items.size());
        assertEquals("Name", items.get(0).getName());
        assertEquals("Description", items.get(0).getDescription());
        assertEquals(user.getId(), items.get(0).getOwner().getId());
        assertEquals(itemRequest.getId(), items.get(0).getRequest().getId());
    }

    @Test
    void search_whenInvoke_returnMatchingItems() {
        List<Item> items = itemRepository.search("Name");

        assertEquals(1, items.size());
        assertEquals("Name", items.get(0).getName());
        assertEquals("Description", items.get(0).getDescription());
        assertEquals(user.getId(), items.get(0).getOwner().getId());
    }
}