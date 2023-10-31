package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemService itemService;

    @SneakyThrows
    @Test
    void getItemsByOwner() {
        Long ownerId = 1L;
        ItemWithBookingDto itemDto = ItemWithBookingDto.builder()
                .id(1L)
                .name("Item 1")
                .build();
        List<ItemWithBookingDto> items = Arrays.asList(itemDto);

        when(itemService.getAllItemOwner(ownerId)).thenReturn(items);

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", ownerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(itemDto.getId()))
                .andExpect(jsonPath("$[0].name").value(itemDto.getName()));

        verify(itemService, times(1)).getAllItemOwner(ownerId);
    }

    @SneakyThrows
    @Test
    void find() {
        Long userId = 1L;
        Long itemId = 1L;
        ItemWithBookingDto itemDto = ItemWithBookingDto.builder()
                .id(itemId)
                .name("Item 1")
                .build();

        when(itemService.getItemById(itemId, userId)).thenReturn(itemDto);

        mockMvc.perform(get("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDto.getId()))
                .andExpect(jsonPath("$.name").value(itemDto.getName()));

        verify(itemService, times(1)).getItemById(itemId, userId);
    }

    @SneakyThrows
    @Test
    void create() {
        Long ownerId = 1L;
        ItemDto itemDto = ItemDto.builder()
                .name("Item 1")
                .description("description 1")
                .available(true)
                .requestId(1L)
                .build();
        ItemDto createdItem = ItemDto.builder()
                .id(1L)
                .name("Item 1")
                .description("description 1")
                .available(true)
                .requestId(1L)
                .build();

        when(itemService.create(itemDto, ownerId)).thenReturn(createdItem);
        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", ownerId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(jsonPath("$.id").value(createdItem.getId()))
                .andExpect(jsonPath("$.name").value(createdItem.getName()));

        verify(itemService, times(1)).create(itemDto, ownerId);
    }

    @SneakyThrows
    @Test
    void update() {
        Long ownerId = 1L;
        Long itemId = 1L;
        ItemDto itemDto = ItemDto.builder()
                .name("Updated Item")
                .build();
        ItemDto updatedItem = ItemDto.builder()
                .id(itemId)
                .name("Updated Item")
                .build();

        when(itemService.update(itemDto, itemId, ownerId)).thenReturn(updatedItem);

        mockMvc.perform(patch("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", ownerId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(updatedItem.getId()))
                .andExpect(jsonPath("$.name").value(updatedItem.getName()));

        verify(itemService, times(1)).update(itemDto, itemId, ownerId);
    }

    @SneakyThrows
    @Test
    void getItemsBySearchQuery() {
        String searchQuery = "item";
        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("Item 1")
                .build();
        List<ItemDto> items = Arrays.asList(itemDto);

        when(itemService.getItemsBySearch(searchQuery)).thenReturn(items);

        mockMvc.perform(get("/items/search")
                        .param("text", searchQuery))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(itemDto.getId()))
                .andExpect(jsonPath("$[0].name").value(itemDto.getName()));

        verify(itemService, times(1)).getItemsBySearch(searchQuery);
    }

    @SneakyThrows
    @Test
    void createComment() {
        Long userId = 1L;
        Long itemId = 1L;
        CommentDto commentDto = CommentDto.builder()
                .text("Comment 1")
                .build();
        CommentDto createdComment = CommentDto.builder()
                .id(1L)
                .text("Comment 1")
                .build();

        when(itemService.createComment(userId, itemId, commentDto)).thenReturn(createdComment);

        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(createdComment.getId()))
                .andExpect(jsonPath("$.text").value(createdComment.getText()));

        verify(itemService, times(1)).createComment(userId, itemId, commentDto);
    }

    @SneakyThrows
    @Test
    void deleteUser() {
        Long itemId = 1L;
        mockMvc.perform(delete("/items/{id}", itemId));
        //.andDo(print());
        verify(itemService).delete(itemId);
    }
}