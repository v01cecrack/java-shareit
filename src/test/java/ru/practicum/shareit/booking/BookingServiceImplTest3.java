package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest3 {

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private BookingServiceImpl bookingService;

    private User user;
    private Booking booking1;
    private Booking booking2;
    private Booking booking3;

    private User owner;
    private UserDto ownerDto;
    private User booker;
    private ItemDto itemDto;
    private BookingDto bookingDto;

    private Item item;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1);
        user.setName("Test User");
        user.setEmail("user@example.com");

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
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusWeeks(2))
                .bookerId(1)
                .itemId(1)
                .build();

        item = Item.builder().id(1).owner(owner).available(true).name("dfsadfsafsd").build();

        booking1 = new Booking();
        booking1.setId(1);
        booking1.setStart(LocalDateTime.now().minusDays(2));
        booking1.setEnd(LocalDateTime.now().minusDays(1));
        booking1.setBooker(user);
        booking1.setItem(item);

        booking2 = new Booking();
        booking2.setId(2);
        booking2.setStart(LocalDateTime.now().plusDays(1));
        booking2.setEnd(LocalDateTime.now().plusDays(2));
        booking2.setBooker(user);
        booking2.setItem(item);

        booking3 = new Booking();
        booking3.setId(3);
        booking3.setStart(LocalDateTime.now().minusHours(1));
        booking3.setEnd(LocalDateTime.now().plusHours(1));
        booking3.setBooker(user);
    }

    @Test
    void getBookingList_PastState_ReturnsPastBookings() {
        List<Booking> pastBookings = Collections.singletonList(booking1);
        Page<Booking> pagePastBookings = new PageImpl<>(pastBookings);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(bookingRepository.findByBooker_IdAndEndIsBeforeOrderByStartDesc(eq(user.getId()), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(pagePastBookings);

        List<BookingDto> result = bookingService.getBookingList(State.PAST, user.getId(), 0, 20);

        assertThat(result).hasSize(1);
        assertBookingDtoEqualsBooking(result.get(0), booking1);
    }

    @Test
    void getBookingList_FutureState_ReturnsFutureBookings() {
        List<Booking> futureBookings = Collections.singletonList(booking2);
        Page<Booking> pageFutureBookings = new PageImpl<>(futureBookings);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(bookingRepository.findByBooker_IdAndStartIsAfterOrderByStartDesc(eq(user.getId()), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(pageFutureBookings);

        List<BookingDto> result = bookingService.getBookingList(State.FUTURE, user.getId(), 0, 20);

        assertThat(result).hasSize(1);
        assertBookingDtoEqualsBooking(result.get(0), booking2);
    }


    private void assertBookingDtoEqualsBooking(BookingDto dto, Booking booking) {
        assertEquals(dto.getId(), booking.getId());
        assertEquals(dto.getStart(), booking.getStart());
        assertEquals(dto.getEnd(), booking.getEnd());
    }

    @Test
    void getBookingList_FutureState_ReturnsCurrentBookings() {
        List<Booking> futureBookings = Collections.singletonList(booking2);
        Page<Booking> pageFutureBookings = new PageImpl<>(futureBookings);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(bookingRepository.findByBooker_IdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(eq(user.getId()), any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(pageFutureBookings);

        List<BookingDto> result = bookingService.getBookingList(State.CURRENT, user.getId(), 0, 20);

        assertThat(result).hasSize(1);
        assertBookingDtoEqualsBooking(result.get(0), booking2);
    }

    @Test
    void getBookingList_PastState_ReturnsWaitingBookings() {
        List<Booking> pastBookings = Collections.singletonList(booking1);
        Page<Booking> pagePastBookings = new PageImpl<>(pastBookings);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(bookingRepository.findByBooker_IdAndStatusOrderByStartDesc(eq(user.getId()), eq(Status.WAITING), any(Pageable.class)))
                .thenReturn(pagePastBookings);

        List<BookingDto> result = bookingService.getBookingList(State.WAITING, user.getId(), 0, 20);

        assertThat(result).hasSize(1);
        assertBookingDtoEqualsBooking(result.get(0), booking1);
    }

    @Test
    void getBookingList_PastState_ReturnsRejectedBookings() {
        List<Booking> pastBookings = Collections.singletonList(booking1);
        Page<Booking> pagePastBookings = new PageImpl<>(pastBookings);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(bookingRepository.findByBooker_IdAndStatusOrderByStartDesc(eq(user.getId()), eq(Status.REJECTED), any(Pageable.class)))
                .thenReturn(pagePastBookings);

        List<BookingDto> result = bookingService.getBookingList(State.REJECTED, user.getId(), 0, 20);

        assertThat(result).hasSize(1);
        assertBookingDtoEqualsBooking(result.get(0), booking1);
    }

    @Test
    void getBookingByItemOwner_PastState_ReturnsPastBookings() {
        List<Booking> pastBookings = Collections.singletonList(booking1);
        Page<Booking> pagePastBookings = new PageImpl<>(pastBookings);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(bookingRepository.findByItemOwnerIdAndEndIsBeforeOrderByStartDesc(eq(user.getId()), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(pagePastBookings);

        List<BookingDto> result = bookingService.getBookingByItemOwner(user.getId(), State.PAST, 0, 20);

        assertThat(result).hasSize(1);
        assertBookingDtoEqualsBooking(result.get(0), booking1);
    }

    @Test
    void getBookingByItemOwner_FutureState_ReturnsFutureBookings() {
        List<Booking> futureBookings = Collections.singletonList(booking2);
        Page<Booking> pageFutureBookings = new PageImpl<>(futureBookings);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(bookingRepository.findByItemOwnerIdAndStartIsAfterOrderByStartDesc(eq(user.getId()), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(pageFutureBookings);

        List<BookingDto> result = bookingService.getBookingByItemOwner(user.getId(), State.FUTURE, 0, 20);

        assertThat(result).hasSize(1);
        assertBookingDtoEqualsBooking(result.get(0), booking2);
    }

    @Test
    void getBookingByItemOwner_FutureState_ReturnsCurrentBookings() {
        List<Booking> futureBookings = Collections.singletonList(booking2);
        Page<Booking> pageFutureBookings = new PageImpl<>(futureBookings);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(bookingRepository.findByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(eq(user.getId()), any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(pageFutureBookings);

        List<BookingDto> result = bookingService.getBookingByItemOwner(user.getId(), State.CURRENT, 0, 20);

        assertThat(result).hasSize(1);
        assertBookingDtoEqualsBooking(result.get(0), booking2);
    }

    @Test
    void getBookingByItemOwner_FutureState_ReturnsWaitingBookings() {
        List<Booking> futureBookings = Collections.singletonList(booking2);
        Page<Booking> pageFutureBookings = new PageImpl<>(futureBookings);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(eq(user.getId()), eq(Status.WAITING), any(Pageable.class)))
                .thenReturn(pageFutureBookings);

        List<BookingDto> result = bookingService.getBookingByItemOwner(user.getId(), State.WAITING, 0, 20);

        assertThat(result).hasSize(1);
        assertBookingDtoEqualsBooking(result.get(0), booking2);
    }

    @Test
    void getBookingByItemOwner_FutureState_ReturnsRejectedBookings() {
        List<Booking> futureBookings = Collections.singletonList(booking2);
        Page<Booking> pageFutureBookings = new PageImpl<>(futureBookings);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(eq(user.getId()), eq(Status.REJECTED), any(Pageable.class)))
                .thenReturn(pageFutureBookings);

        List<BookingDto> result = bookingService.getBookingByItemOwner(user.getId(), State.REJECTED, 0, 20);

        assertThat(result).hasSize(1);
        assertBookingDtoEqualsBooking(result.get(0), booking2);
    }

    @Test
    void getBookingByItemOwner_FutureState_ReturnsDefaultBookings() {
        List<Booking> futureBookings = Collections.singletonList(booking2);
        Page<Booking> pageFutureBookings = new PageImpl<>(futureBookings);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(bookingRepository.findByItemOwnerIdOrderByStartDesc(eq(user.getId()), any(Pageable.class)))
                .thenReturn(pageFutureBookings);

        List<BookingDto> result = bookingService.getBookingByItemOwner(user.getId(), State.ALL, 0, 20);

        assertThat(result).hasSize(1);
        assertBookingDtoEqualsBooking(result.get(0), booking2);
    }

}
