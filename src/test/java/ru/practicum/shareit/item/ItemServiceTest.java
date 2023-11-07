package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dao.BookingJpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.dao.CommentJpaRepository;
import ru.practicum.shareit.item.dao.ItemJpaRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;
import ru.practicum.shareit.item.exeption.ItemIdException;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dao.UserJpaRepository;
import ru.practicum.shareit.user.exeption.UserIdException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @Mock
    private ItemJpaRepository itemRepository;
    @Mock
    private UserJpaRepository userRepository;
    @Mock
    private BookingJpaRepository bookingRepository;
    @Mock
    private CommentJpaRepository commentRepository;
    @InjectMocks
    private ItemService itemService;

    private Long userId;
    private Long itemId;
    private Long requestId;
    private ItemDto itemDto;
    private User user;
    private ItemRequest itemRequest;
    private Item item;
    private Booking booking;
    private CommentDto commentDto;
    private Comment comment;

    @BeforeEach
    void setUp() {
        userId = 1L;
        itemId = 2L;
        requestId = 3L;
        user = User.builder()
                .id(userId)
                .name("userName")
                .email("em@em.em")
                .build();
        itemRequest = ItemRequest.builder()
                .id(requestId)
                .description("request description")
                .requester(user)
                .created(LocalDateTime.now())
                .build();
        itemDto = ItemDto.builder()
                .id(itemId)
                .name("name")
                .description("description")
                .available(true)
                .requestId(requestId)
                .build();
        item = Item.builder()
                .id(itemId)
                .name("name")
                .description("description")
                .available(true)
                .request(itemRequest)
                .owner(user)
                .build();
        booking = new Booking();
        commentDto = new CommentDto();
        comment = new Comment();

    }


    @Test
    void createItem_whenUserAndRequestNotFound_thenReturnDto() {
        item.setRequest(null);
        itemDto.setRequestId(null);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        ItemDto newItemDto = itemService.create(itemDto, userId);

        assertEquals(newItemDto, itemDto);
    }

    @Test
    void createItem_whenUserNotFound_thenNotFoundExceptionThrown() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserIdException.class, () -> itemService.create(itemDto, userId));
    }

    @Test
    void findItem_whenUserFound_thenReturnDto() {
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        ItemWithBookingDto newItemDto = itemService.getItemById(itemId, userId);

        assertEquals(newItemDto.getName(), itemDto.getName());
    }


    @Test
    void getAllItemOwner_whenUserFound_thenReturnDtoList() {
        List<ItemDto> expectedItemsDto = new ArrayList<>(List.of(itemDto));
        List<Item> items = new ArrayList<>(List.of(item));
        when(itemRepository.findAllByOwner_IdOrderById(userId))
                .thenReturn(items);

        List<ItemWithBookingDto> newItemsDto = itemService.getAllItemOwner(userId);

        assertEquals(expectedItemsDto.get(0).getName(), newItemsDto.get(0).getName());
    }

    @Test
    void getItemsBySearch_whenUserFound_thenReturnDtoList() {
        List<ItemDto> expectedItemsDto = new ArrayList<>(List.of(itemDto));
        List<Item> items = new ArrayList<>(List.of(item));
        when(itemRepository.search("request")).thenReturn(items);

        List<ItemDto> newItemsDto = itemService.getItemsBySearch("request");

        assertEquals(expectedItemsDto, newItemsDto);
        verify(itemRepository).search("request");
    }

    @Test
    void getItemsBySearch_whenEmpty_thenReturnEmptyList() {
        List<ItemDto> newItemsDto = itemService.getItemsBySearch("");
        assertEquals(List.of(), newItemsDto);
    }

    @Test
    void getAllItemOwner_whenEmpty_thenReturnEmptyList() {
        when(itemRepository.findAllByOwner_IdOrderById(userId)).thenReturn(List.of());
        List<ItemWithBookingDto> newItemsDto = itemService.getAllItemOwner(userId);
        assertEquals(List.of(), newItemsDto);
    }

    @Test
    void getItemById_whenEmpty_thenReturnEmptyList() {
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());
        assertThrows(ItemIdException.class, () -> itemService.getItemById(itemId, userId));

    }

    @Test
    void updateItem_whenItemNotFound_thenNItemIdException() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(ItemIdException.class, () -> itemService.update(itemDto, itemId, userId));

    }


    @Test
    void updateItem_whenItemFound_thenReturnDto() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        ItemDto newItemDto = itemService.update(itemDto, itemId, userId);

        assertEquals(newItemDto, itemDto);
    }

    @Test
    void deleteItem_whenItemFound_thenDelete() {

        itemService.delete(itemId);

        verify(itemRepository, times(1)).deleteById(itemId);
    }


    @Test
    void createComment_whenStatePast_thenReturnCommentDto() {
        LocalDateTime current = LocalDateTime.now();
        booking.setBooker(user);
        booking.setStatus(Status.APPROVED);
        booking.setStart(current.minusDays(2));
        booking.setEnd(current.minusDays(1));
        comment.setItem(item);
        comment.setUser(user);
        comment.setText("comment");
        commentDto.setItemId(itemId);
        commentDto.setAuthorName("userName");
        commentDto.setText("comment");
        when(bookingRepository
                .existsByItem_IdAndEndBeforeAndStatusAndBooker_Id(any(Long.class),
                        any(LocalDateTime.class),
                        any(Status.class),
                        any(Long.class)))
                .thenReturn(true);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        CommentDto newCommentDto = itemService.createComment(userId, itemId, commentDto);

        assertEquals(newCommentDto, commentDto);
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

}