package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.error.ObjectNotFoundException;
import ru.practicum.shareit.error.ValidationException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private BookingServiceImpl bookingService;
    private User owner;
    private UserDto ownerDto;
    private User booker;
    private ItemDto itemDto;
    private BookingDto bookingDto;

    private int from;
    private int size;

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
        bookingDto = BookingDto.builder()
                .id(1)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusWeeks(2))
                .bookerId(1)
                .itemId(1)
                .build();

        ownerDto = UserMapper.toUserDto(owner);


        from = 0;
        size = 20;
    }


    @Test
    void addBooking() {
        int userId = booker.getId();
        int itemId = bookingDto.getItemId();

        Item item = ItemMapper.toItem(itemDto, owner);
        Booking booking = BookingMapper.toBooking(bookingDto, item);
        booking.setBooker(booker);
        booking.setStatus(Status.WAITING);

        when(userRepository.findById(userId)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingRepository.findBookingByItem_Id(itemId)).thenReturn(Collections.emptyList());
        when(bookingRepository.save(booking)).thenReturn(booking);

        BookingDto actualBookingDto = bookingService.createBooking(bookingDto, userId);

        assertNotNull(actualBookingDto);
        assertEquals(bookingDto.getStart(), actualBookingDto.getStart());
        verify(bookingRepository).save(any());
    }

    @Test
    void addBookingIsOwner_ThenReturnThrow() {
        int userId = owner.getId();
        int itemId = bookingDto.getItemId();

        Item item = ItemMapper.toItem(itemDto, owner);
//        item.setOwner(owner);

        Booking booking = BookingMapper.toBooking(bookingDto, item);
        booking.setStatus(Status.WAITING);
        booking.setBooker(booker);
//        booking.setItem(item);

        when(userRepository.findById(userId)).thenReturn(Optional.of(owner));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class,
                () -> bookingService.createBooking(bookingDto, userId));

        assertThat(exception.getMessage(), equalTo("Вы владелец этой вещи"));
        verify(bookingRepository, never()).save(any());

    }


    @Test
    void approveBooking() {
        int bookingId = bookingDto.getId();
        int userId = owner.getId();
        Item item = ItemMapper.toItem(itemDto, owner);

        Booking booking = BookingMapper.toBooking(bookingDto, item);
        booking.setStatus(Status.WAITING);
        booking.setBooker(booker);
        when(userRepository.findById(userId)).thenReturn(Optional.ofNullable(owner));
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(booking)).thenReturn(booking);

        BookingDto result = bookingService.approveBooking(bookingId, userId, true);

        assertThat(result.getStatus(), equalTo(Status.APPROVED));
        verify(bookingRepository).findById(bookingId);
        verify(bookingRepository).save(any());
    }

    @Test
    void approvedWhenStatusNotApproved_ThenThrowsValidationException() {
        int bookingId = bookingDto.getId();

        Item item = ItemMapper.toItem(itemDto, owner);
//        item.setOwner(owner);

        Booking booking = BookingMapper.toBooking(bookingDto, item);
        booking.setItem(item);
        booking.setStatus(Status.APPROVED);
        booking.setBooker(booker);

//        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class,
                () -> bookingService.approveBooking(owner.getId(), bookingId, true));

        assertThat(exception.getMessage(), equalTo("Владелец не найден"));
        verify(bookingRepository, never()).save(any());

    }

    @Test
    void getBooking() {
        int bookingId = bookingDto.getId();

        Item item = ItemMapper.toItem(itemDto, owner);

        Booking booking = BookingMapper.toBooking(bookingDto, item);
        booking.setBooker(booker);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        BookingDto result = bookingService.getBooking(booker.getId(), bookingId);

        assertNotNull(result);
        assertEquals(booking.getId(), result.getId());

        verify(bookingRepository, times(1)).findById(bookingId);
    }

    @Test
    void addBooking_ItemNotAvailable_ThenThrowValidationException() {
        int userId = owner.getId();
        int itemId = bookingDto.getItemId();

        Item item = ItemMapper.toItem(itemDto, booker);
        item.setAvailable(false);

        when(userRepository.findById(userId)).thenReturn(Optional.of(owner));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        ValidationException exception = assertThrows(ValidationException.class,
                () -> bookingService.createBooking(bookingDto, userId));

        assertThat(exception.getMessage(), equalTo("Данная вещь недоступна"));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void addBooking_ItemAlreadyBooked_ThenThrowValidationException() {
        int userId = owner.getId();
        int itemId = bookingDto.getItemId();

        Item item = ItemMapper.toItem(itemDto, booker);
        item.setAvailable(true);

        Booking existingBooking = new Booking();
        existingBooking.setItem(item);
        existingBooking.setStart(bookingDto.getStart());
        existingBooking.setEnd(bookingDto.getEnd());

        when(userRepository.findById(userId)).thenReturn(Optional.of(owner));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingRepository.findBookingByItem_Id(itemId)).thenReturn(Collections.singletonList(existingBooking));

        ValidationException exception = assertThrows(ValidationException.class,
                () -> bookingService.createBooking(bookingDto, userId));

        assertThat(exception.getMessage(), equalTo("Данная вещь уже забронирована"));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void addBooking_ItemAlreadyBooked1_ThenThrowValidationException() {
        int userId = owner.getId();
        int itemId = bookingDto.getItemId();

        Item item = ItemMapper.toItem(itemDto, booker);
        item.setAvailable(true);

        Booking existingBooking = new Booking();
        existingBooking.setItem(item);
        existingBooking.setStart(bookingDto.getStart().minusHours(2));
        existingBooking.setEnd(bookingDto.getEnd().plusHours(2));

        when(userRepository.findById(userId)).thenReturn(Optional.of(owner));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingRepository.findBookingByItem_Id(itemId)).thenReturn(Collections.singletonList(existingBooking));

        ValidationException exception = assertThrows(ValidationException.class,
                () -> bookingService.createBooking(bookingDto, userId));

        assertThat(exception.getMessage(), equalTo("Данная вещь уже забронирована"));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void addBooking_ItemAlreadyBooked2_ThenThrowValidationException() {
        int userId = owner.getId();
        int itemId = bookingDto.getItemId();

        Item item = ItemMapper.toItem(itemDto, booker);
        item.setAvailable(true);

        Booking existingBooking = new Booking();
        existingBooking.setItem(item);
        existingBooking.setStart(bookingDto.getStart().plusHours(2));
        existingBooking.setEnd(bookingDto.getEnd().minusHours(2));

        when(userRepository.findById(userId)).thenReturn(Optional.of(owner));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingRepository.findBookingByItem_Id(itemId)).thenReturn(Collections.singletonList(existingBooking));

        ValidationException exception = assertThrows(ValidationException.class,
                () -> bookingService.createBooking(bookingDto, userId));

        assertThat(exception.getMessage(), equalTo("Данная вещь уже забронирована"));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void addBooking_ItemAlreadyBooked3_ThenThrowValidationException() {
        int userId = owner.getId();
        int itemId = bookingDto.getItemId();

        Item item = ItemMapper.toItem(itemDto, booker);
        item.setAvailable(true);

        Booking existingBooking = new Booking();
        existingBooking.setItem(item);
        existingBooking.setStart(bookingDto.getStart().minusHours(2));
        existingBooking.setEnd(bookingDto.getEnd().minusDays(2));

        when(userRepository.findById(userId)).thenReturn(Optional.of(owner));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingRepository.findBookingByItem_Id(itemId)).thenReturn(Collections.singletonList(existingBooking));

        ValidationException exception = assertThrows(ValidationException.class,
                () -> bookingService.createBooking(bookingDto, userId));

        assertThat(exception.getMessage(), equalTo("Данная вещь уже забронирована"));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void addBooking_ItemAlreadyBooked4_ThenThrowValidationException() {
        int userId = owner.getId();
        int itemId = bookingDto.getItemId();

        Item item = ItemMapper.toItem(itemDto, booker);
        item.setAvailable(true);

        Booking existingBooking = new Booking();
        existingBooking.setItem(item);
        existingBooking.setStart(bookingDto.getStart().plusHours(2));
        existingBooking.setEnd(bookingDto.getEnd().plusHours(2));

        when(userRepository.findById(userId)).thenReturn(Optional.of(owner));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingRepository.findBookingByItem_Id(itemId)).thenReturn(Collections.singletonList(existingBooking));

        ValidationException exception = assertThrows(ValidationException.class,
                () -> bookingService.createBooking(bookingDto, userId));

        assertThat(exception.getMessage(), equalTo("Данная вещь уже забронирована"));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void getBookingByItemOwner_InvalidUser_ThrowsObjectNotFoundException() {
        int userId = 1;
        State state = State.PAST;
        int from = 0;
        int size = 20;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class,
                () -> bookingService.getBookingByItemOwner(userId, state, from, size));

        assertEquals("Пользователь не найден", exception.getMessage());

        verify(userRepository).findById(userId);
        verify(bookingRepository, never()).findByItemOwnerIdAndEndIsBeforeOrderByStartDesc(anyInt(), any(LocalDateTime.class), any(PageRequest.class));
    }


}
