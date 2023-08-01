package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.item.dao.CommentRepository;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest2 {
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    @InjectMocks
    private ItemServiceImpl itemService;

    private User owner;
    private ItemDto itemDto;
    private List<Item> items;

    private int from;
    private int size;

    @BeforeEach
    void setUser() {
        owner = User.builder()
                .id(1)
                .name("owner")
                .email("owner@email.com")
                .build();

        itemDto = ItemDto.builder()
                .id(1)
                .name("item")
                .description("description")
                .owner(owner)
                .build();

        items = new ArrayList<>();
        items.add(Item.builder().id(1).name("item1").owner(owner).build());
        items.add(Item.builder().id(2).name("item2").owner(owner).build());
        items.add(Item.builder().id(3).name("item3").owner(owner).build());

        from = 0;
        size = 2;
    }

    @Test
    void getItems_WithValidData_ReturnsItemDtos() {
        int userId = 1;
        int from = 0;
        int size = 10;

        List<Item> items = new ArrayList<>();
        items.add(Item.builder().id(1).owner(User.builder().id(userId).build()).owner(new User(1, "dsfds", "sss@mail.ru")).build());
        items.add(Item.builder().id(2).owner(User.builder().id(userId).build()).build());

        List<Booking> bookings = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        bookings.add(Booking.builder().id(1).item(items.get(0)).status(Status.APPROVED).start(now.minusDays(2)).end(now.minusDays(1)).booker(new User(4, "f", "kostyaa@gmail.com")).build());
        bookings.add(Booking.builder().id(2).item(items.get(0)).status(Status.APPROVED).start(now.plusDays(1)).end(now.plusDays(2)).booker(new User(3, "f", "kostya@gmail.com")).build());

        List<Comment> comments = new ArrayList<>();
        comments.add(Comment.builder().id(1).item(items.get(0)).author(new User(5, "ess", "kkk@gmail.com")).build());
        comments.add(Comment.builder().id(2).item(items.get(1)).author(new User(6, "ess", "kdkk@gmail.com")).build());

        when(itemRepository.findItemByOwnerIdOrderById(userId, PageRequest.of(0, 10))).thenReturn(new PageImpl<>(items));
        when(bookingRepository.findBookingByStatus(Status.APPROVED)).thenReturn(bookings);
        when(commentRepository.findCommentsByItem_Id(1)).thenReturn(comments.stream().filter(c -> c.getItem().getId() == 1).collect(Collectors.toList()));
        when(commentRepository.findCommentsByItem_Id(2)).thenReturn(comments.stream().filter(c -> c.getItem().getId() == 2).collect(Collectors.toList()));

        List<ItemDto> actualItemDtos = itemService.getItems(userId, from, size);

        assertEquals(2, actualItemDtos.size());

        ItemDto itemDto1 = actualItemDtos.get(0);
        assertEquals(1, itemDto1.getId());
        assertEquals(1, itemDto1.getComments().size());
        assertNotNull(itemDto1.getLastBooking());
        assertNotNull(itemDto1.getNextBooking());

        ItemDto itemDto2 = actualItemDtos.get(1);
        assertEquals(2, itemDto2.getId());
        assertEquals(1, itemDto2.getComments().size());
        assertNull(itemDto2.getLastBooking());
        assertNull(itemDto2.getNextBooking());
    }

    @Test
    void searchItems_WithValidText_ReturnsFilteredItemDtos() {
        String searchText = "phone";
        int from = 0;
        int size = 10;

        List<Item> items = new ArrayList<>();
        items.add(Item.builder().id(1).name("iPhone 12").available(true).build());
        items.add(Item.builder().id(2).name("Samsung Galaxy S21").available(false).build());
        items.add(Item.builder().id(3).name("Laptop").available(true).build());

        when(itemRepository.search(searchText, PageRequest.of(0, 10))).thenReturn(new PageImpl<>(items));

        List<ItemDto> actualItemDtos = itemService.searchItems(searchText, from, size);

        assertEquals(2, actualItemDtos.size());

        ItemDto itemDto1 = actualItemDtos.get(0);
        assertEquals(1, itemDto1.getId());
        assertEquals("iPhone 12", itemDto1.getName());
        assertTrue(itemDto1.getAvailable());

        ItemDto itemDto3 = actualItemDtos.get(1);
        assertEquals(3, itemDto3.getId());
        assertEquals("Laptop", itemDto3.getName());
        assertTrue(itemDto3.getAvailable());
    }
}