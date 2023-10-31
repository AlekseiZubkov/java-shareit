package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemRequestControllerTest {

    @Mock
    private ItemRequestService itemRequestService;

    @InjectMocks
    private ItemRequestController itemRequestController;

    private Long userId = 1L;
    private Long requestId = 1L;
    private ItemRequestDto itemRequestDto1;
    private ItemRequestDto itemRequestDto2;

    @BeforeEach
    void setUp() {
        ItemDto itemDto1 = new ItemDto();
        ItemDto itemDto2 = new ItemDto();

        itemRequestDto1 = ItemRequestDto.builder()
                .id(requestId)
                .description("Request 1")
                .requester(userId)
                .created(LocalDateTime.now())
                .items(Arrays.asList(itemDto1, itemDto2))
                .build();

        itemRequestDto2 = ItemRequestDto.builder()
                .id(2L)
                .description("Request 2")
                .requester(userId)
                .created(LocalDateTime.now())
                .items(Arrays.asList(itemDto1))
                .build();
    }

    @Test
    void createItemRequest() {
        when(itemRequestService.create(userId, itemRequestDto1)).thenReturn(itemRequestDto1);

        ItemRequestDto createdItemRequestDto = itemRequestController.create(userId, itemRequestDto1);

        assertEquals(itemRequestDto1, createdItemRequestDto);
        verify(itemRequestService).create(userId, itemRequestDto1);
    }

    @Test
    void getItemRequests() {
        List<ItemRequestDto> expectedItemRequests = Arrays.asList(itemRequestDto1, itemRequestDto2);
        when(itemRequestService.get(userId)).thenReturn(expectedItemRequests);

        List<ItemRequestDto> actualItemRequests = itemRequestController.get(userId);

        assertEquals(expectedItemRequests, actualItemRequests);
        verify(itemRequestService).get(userId);
    }

    @Test
    void getAllItemRequests() {
        List<ItemRequestDto> expectedItemRequests = Arrays.asList(itemRequestDto1, itemRequestDto2);
        Long from = 0L;
        Long size = 10L;
        when(itemRequestService.getAll(userId, from, size)).thenReturn(expectedItemRequests);

        List<ItemRequestDto> actualItemRequests = itemRequestController.get(userId, from, size);

        assertEquals(expectedItemRequests, actualItemRequests);
        verify(itemRequestService).getAll(userId, from, size);
    }

    @Test
    void getItemRequestById() {
        when(itemRequestService.getByID(userId, requestId)).thenReturn(itemRequestDto1);

        ItemRequestDto actualItemRequest = itemRequestController.get(userId, requestId);

        assertEquals(itemRequestDto1, actualItemRequest);
        verify(itemRequestService).getByID(userId, requestId);
    }
}