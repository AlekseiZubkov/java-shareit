package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.item.dao.ItemJpaRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.exeption.ItemRequestParamException;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dao.UserJpaRepository;
import ru.practicum.shareit.user.exeption.UserIdException;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceTest {

    @Mock
    private ItemJpaRepository itemRepository;
    @Mock
    private UserJpaRepository userRepository;
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @InjectMocks
    private ItemRequestService itemRequestService;

    private Long userId = 1L;
    private User user;
    private ItemRequest itemRequest;
    private ItemRequestDto itemRequestDto;
    private Item item;
    private ItemDto itemDto;
    private Long requestId;

    private ItemRequestDto itemRequestDto1;
    private ItemRequestDto itemRequestDto2;


    @BeforeEach
    void setUp() {
        userId = 1L;
        requestId = 1L;
        user = new User(1L, "Alex", "alex@email.ru");
        itemRequestDto1 = ItemRequestDto.builder()
                .id(1L)
                .description("description")
                .requester(user.getId())
                .created(LocalDateTime.now())
                .items(new ArrayList<>())
                .build();
        ;
        itemRequestDto2 = new ItemRequestDto();
        itemRequest = ItemRequest.builder()
                .id(1L)
                .description("description")
                .requester(user)
                .created(LocalDateTime.now())
                .build();
        item = new Item(1L, "Test Item", "Test Description", true, user, itemRequest);

    }

    @Test
    void createRequest_whenUserNotFound_thenNotFoundUser() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserIdException.class,
                () -> itemRequestService.create(userId, itemRequestDto1));
    }

    @Test
    void createRequest_whenUserFound_thenReturnRequestDto() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRequestRepository.save(any(ItemRequest.class))).thenReturn(itemRequest);

        itemRequestDto2 = itemRequestService.create(userId, itemRequestDto1);

        assertEquals(itemRequestDto1.getId(), itemRequestDto2.getId());
        assertEquals(itemRequestDto1.getDescription(), itemRequestDto2.getDescription());
    }

    @Test
    void findByUserId_whenUserNotFound_thenNotFoundUser() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserIdException.class,
                () -> itemRequestService.get(userId));
    }

    @Test
    void findByUserIdAndRequestId_whenUserNotFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserIdException.class,
                () -> itemRequestService.getByID(userId, requestId));
    }

    @Test
    void getByID_throwItemRequestParamException() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(requestId)).thenReturn(Optional.empty());
        assertThrows(ItemRequestParamException.class,
                () -> itemRequestService.getByID(userId, requestId));
    }

    @Test
    void get_Empty() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRequestRepository.findAllByRequesterId(requestId)).thenReturn(new ArrayList<>());
        List<ItemRequestDto> itemRequestDtos = itemRequestService.get(userId);
        assertEquals(itemRequestDtos, List.of());
    }

    @Test
    void getByID_Valid() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(requestId)).thenReturn(Optional.of(itemRequest));

        ItemRequestDto itemRequestDto3 = itemRequestService.getByID(userId, requestId);

        assertEquals(itemRequestDto3.getId(), itemRequestDto1.getId());
    }

    @Test
    void findAllByUserId_whenArgumentField() {
        assertThrows(ValidationException.class,
                () -> itemRequestService.getAll(userId, 0L, 0L));
    }

    @Test
    void findAllByUserId_whenArgumentFieldMinus() {
        assertThrows(ValidationException.class,
                () -> itemRequestService.getAll(userId, 0L, -100L));
    }

    @Test
    void findAllByUser_whenArgumentField() {
        PageRequest pageRequest = PageRequest.of(0 / 5, 5,
                Sort.Direction.DESC, "created");
        when(itemRequestRepository.findAll(userId, pageRequest)).thenReturn(Collections.emptyList());
        List<ItemRequestDto> emptyList = Collections.unmodifiableList(itemRequestService
                .getAll(userId, 0L, 5L));
        assertTrue(emptyList.isEmpty());
    }

    @Test
    void createItemRequest_invalidUser_throwUserIdException() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserIdException.class, () -> itemRequestService.create(userId, itemRequestDto1));
        verify(userRepository).findById(userId);

    }

    @Test
    void findByUserId_whenUserFound_thenReturnRequestDto() {
        List<ItemRequest> requests = new ArrayList<>(List.of(itemRequest));
        List<ItemRequestDto> itemRequestsDto1 = new ArrayList<>(List.of(itemRequestDto1));
        PageRequest pageRequest = PageRequest.of(0 / 5, 5,
                Sort.Direction.DESC, "created");
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRequestRepository.findAllByRequesterId(userId))
                .thenReturn(requests);
        when(itemRepository.findAllByRequest_id(userId))
                .thenReturn(List.of(item));

        List<ItemRequestDto> itemRequestsDto2 = itemRequestService.get(userId);

        assertEquals(itemRequestsDto2.get(0).getId(), itemRequestsDto1.get(0).getId());
        assertEquals(itemRequestsDto2.get(0).getRequester(), itemRequestsDto1.get(0).getRequester());
    }

    @Test
    void createItemRequest_valid_success() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRequestRepository.save(any(ItemRequest.class))).thenReturn(itemRequest);

        ItemRequestDto result = itemRequestService.create(userId, itemRequestDto1);

        assertEquals(itemRequestDto1.getId(), result.getId());
        assertEquals(itemRequestDto1.getDescription(), result.getDescription());
        verify(userRepository).findById(userId);
        verify(itemRequestRepository).save(any(ItemRequest.class));

    }


    @Test
    void getItemRequests_validUser_success() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRequestRepository.findAllByRequesterId(userId)).thenReturn(Collections.singletonList(itemRequest));
        when(itemRepository.findAllByRequest_id(userId)).thenReturn(Collections.singletonList(item));


        List<ItemRequestDto> result = itemRequestService.get(userId);

        assertEquals(itemRequestDto1.getId(), result.get(0).getId());
        verify(userRepository).findById(userId);
        verify(itemRequestRepository).findAllByRequesterId(userId);
        verify(itemRepository).findAllByRequest_id(userId);
    }

    @Test
    void getItemRequests_invalidUser_throwUserIdException() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserIdException.class, () -> itemRequestService.get(userId));
        verify(userRepository).findById(userId);
        verify(itemRequestRepository, never()).findAllByRequesterId(userId);

    }

    @Test
    void getAllItemRequests_validParams_success() {

        PageRequest pageRequest = PageRequest.of(0 / 5, 5,
                Sort.Direction.DESC, "created");
        List<ItemRequest> itemRequests = Collections.singletonList(itemRequest);

        when(itemRequestRepository.findAll(userId, pageRequest)).thenReturn(itemRequests);
        when(itemRepository.findAllByRequest_id(itemRequest.getId())).thenReturn(Collections.singletonList(item));


        List<ItemRequestDto> result = itemRequestService.getAll(userId, 0L, 5L);

        assertEquals(itemRequestDto1.getId(), result.get(0).getId());
        assertEquals(itemRequestDto1.getDescription(), result.get(0).getDescription());
        verify(itemRequestRepository).findAll(userId, pageRequest);
    }
}