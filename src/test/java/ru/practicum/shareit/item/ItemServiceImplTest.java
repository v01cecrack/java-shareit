package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.error.ObjectNotFoundException;
import ru.practicum.shareit.item.dao.CommentRepository;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {
    @Mock
    private ItemRepository itemRepository;
    @Mock
    ItemRequestRepository itemRequestRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserService userService;
    @Mock
    private CommentRepository commentRepository;
    @InjectMocks
    private ItemServiceImpl itemService;
    private User owner;
    private UserDto ownerDto;
    private User booker;
    private ItemDto itemDto;
    private BookingDto booking;
    private Comment comment;

    @BeforeEach
    void setUser() {
        owner = User.builder()
                .id(2)
                .name("owner")
                .email("email2@email.com")
                .build();

        booker = User.builder()
                .id(1)
                .name("booker")
                .email("email2@email.com")
                .build();
        itemDto = ItemDto.builder()
                .id(1)
                .name("item")
                .description("description")
                .owner(owner)
                .available(true)
                .build();
        booking = BookingDto.builder()
                .id(1)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusWeeks(2))
                .bookerId(1)
                .itemId(1)
                .build();

        ownerDto = UserMapper.toUserDto(owner);
        comment = Comment.builder()
                .id(1L)
                .text("comment1")
                .author(booker)
                .item(ItemMapper.toItem(itemDto, owner))
                .build();

    }


    @Test
    void addItem_InvalidRequest_ShouldThrowObjectNotFoundException() {
        int userId = owner.getId();
        itemDto.setRequestId(123);

        when(userRepository.findById(userId)).thenReturn(Optional.of(owner));

        when(itemRequestRepository.findById(itemDto.getRequestId())).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> itemService.addItem(itemDto, userId));
    }

    @Test
    void updateItem_ValidItem_ShouldReturnItemDto() {
        int userId = owner.getId();

        Item existingItem = ItemMapper.toItem(itemDto, owner);
        when(itemRepository.findById(itemDto.getId())).thenReturn(Optional.of(existingItem));

        when(userRepository.findById(userId)).thenReturn(Optional.of(owner));

        Item expectedItem = ItemMapper.toItem(itemDto, owner);
        when(itemRepository.save(any(Item.class))).thenReturn(expectedItem);

        ItemDto actualItemDto = itemService.editItem(itemDto, userId);

        assertNotNull(actualItemDto);
        assertEquals(actualItemDto.getId(), 1);
        assertEquals(actualItemDto.getName(), "item");

        verify(itemRepository).save(any(Item.class));
    }

    @Test
    void updateItem_UserIsNotOwner_ShouldThrowObjectNotFoundException() {
        int userId = owner.getId();

        assertThrows(ObjectNotFoundException.class, () -> itemService.editItem(itemDto, userId));

        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    void addItem_ValidItem_ShouldReturnItemDto() {
        int userId = owner.getId();

        Item item = ItemMapper.toItem(itemDto, owner);
        when(userRepository.findById(userId)).thenReturn(Optional.of(owner));
        item.setOwner(UserMapper.toUser(ownerDto));
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        ItemDto actualItemDto = itemService.addItem(itemDto, userId);

        assertNotNull(itemDto);
        assertEquals(actualItemDto.getId(), 1);
        assertEquals(actualItemDto.getName(), "item");
        verify(itemRepository).save(any(Item.class));

    }

    @Test
    void getItem() {
        int itemId = itemDto.getId();
        int userId = owner.getId();

        List<Comment> comments = new ArrayList<>();
        var item = ItemMapper.toItem(itemDto, owner);
        comments.add(comment);
        List<Booking> bookings = new ArrayList<>();

        var booking1 = BookingMapper.toBooking(booking, item);
        booking1.setBooker(booker);
        bookings.add(booking1);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(commentRepository.findCommentsByItem_Id(itemId)).thenReturn(comments);
        when(bookingRepository.findBookingByItem_IdAndStatus(itemId, Status.APPROVED)).thenReturn(bookings);

        ItemDto itemDto = itemService.viewItem(itemId, userId);
        verify(itemRepository).findById(itemId);

        assertNotNull(itemDto);
        assertEquals(itemId, itemDto.getId());
        assertEquals(comment.getId(), itemDto.getComments().get(0).getId());
    }

    @Test
    void addComment() {
        int itemId = itemDto.getId();
        int userId = booker.getId();

        var item = ItemMapper.toItem(itemDto, owner);

        List<Booking> bookings = new ArrayList<>();
        var booking1 = BookingMapper.toBooking(booking, item);
        booking1.setBooker(booker);
        booking1.setStatus(Status.APPROVED);
        bookings.add(booking1);

        comment.setItem(item);
        comment.setCreated(LocalDateTime.now());

        var commentDto = CommentMapper.tocommentDto(comment);

        when(itemRepository.findById(eq(itemId))).thenReturn(Optional.of(item));
        when(userRepository.findById(eq(userId))).thenReturn(Optional.of(booker));
        when(bookingRepository.findBookingByItem_Id(eq(itemId))).thenReturn(bookings);
        doReturn(comment).when(commentRepository).save(any(Comment.class));

        CommentDto commentTest = itemService.addComment(userId, itemId, commentDto);

        assertThat(commentTest, notNullValue());
        assertThat(commentTest.getId(), equalTo(comment.getId()));
        verify(userRepository).findById(userId);
        verify(bookingRepository).findBookingByItem_Id(itemId);
        verify(commentRepository).save(any());
        verifyNoMoreInteractions(userRepository, itemRepository, bookingRepository, commentRepository);

    }

}
