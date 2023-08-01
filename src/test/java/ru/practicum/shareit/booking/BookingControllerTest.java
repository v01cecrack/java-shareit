package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.error.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private BookingService bookingService;
    private User booker;
    private User owner;
    private ItemDto item;
    private BookingDto bookingDto;
    private int from;
    private int size;

    @BeforeEach
    @Test
    void setUpBookingDto() {
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
        item = ItemDto.builder()
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

        from = 0;
        size = 20;
    }


    @SneakyThrows
    @Test
    void addBookingRequest(){
        when(bookingService.createBooking(any(BookingDto.class), anyInt())).thenReturn(bookingDto);
        String contentAsString = mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", booker.getId())
                        .content(objectMapper.writeValueAsString(bookingDto)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertEquals(objectMapper.writeValueAsString(bookingDto), contentAsString);
        verify(bookingService).createBooking(bookingDto, booker.getId());
    }


    @SneakyThrows
    @Test
    void approvedBookingRequest() {
        bookingDto.setStatus(Status.APPROVED);
        when(bookingService.approveBooking(anyInt(), anyInt(), anyBoolean())).thenReturn(bookingDto);
        var bookingId = bookingDto.getId();
        var userId = booker.getId();


        String contentAsString = mockMvc.perform(MockMvcRequestBuilders.patch("/bookings/{bookingId}", bookingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", booker.getId())
                        .param("approved", "true")
                        .content(objectMapper.writeValueAsString(bookingDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertEquals(objectMapper.writeValueAsString(bookingDto), contentAsString);
        verify(bookingService).approveBooking(userId, bookingId, true);
    }

    @SneakyThrows
    @Test
    void getBooking() {
        var bookingId = bookingDto.getId();
        var userId = booker.getId();
        when(bookingService.getBooking(userId, bookingId)).thenReturn(bookingDto);
        String contentAsString = mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userId)
                        .content(objectMapper.writeValueAsString(bookingDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(bookingDto), contentAsString);
        verify(bookingService, atLeast(1)).getBooking(bookingId, userId);

    }

    @SneakyThrows
    @Test
    void getBookingList() {
        State state = State.ALL;
        var userId = booker.getId();
        List<BookingDto> bookingDtoList = List.of(bookingDto);
        when(bookingService.getBookingList(state, userId, from, size)).thenReturn(bookingDtoList);

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", "ALL")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(bookingService, times(1)).getBookingList(state, userId, from, size);

    }

    @SneakyThrows
    @Test
    void getBookingByItemOwner() {

        State state = State.ALL;
        var userId = booker.getId();
        List<BookingDto> bookingDtoList = List.of(bookingDto);
        when(bookingService.getBookingByItemOwner(userId, state, from, size)).thenReturn(bookingDtoList);

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", "ALL")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(bookingService, times(1)).getBookingByItemOwner(userId, state, from, size);

    }


    @SneakyThrows
    @Test
    void getBookingList_UnsupportedState_ThrowsValidationException() {
        int userId = booker.getId();
        String unsupportedState = "UNSUPPORTED_STATUS";

        when(bookingService.getBookingList(any(State.class), eq(userId), anyInt(), anyInt()))
                .thenThrow(new ValidationException("Unknown state: " + unsupportedState));

        MvcResult result = mockMvc.perform(get("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userId)
                        .param("state", unsupportedState)
                        .param("from", "0")
                        .param("size", "20"))
                .andExpect(status().isBadRequest())
                .andReturn();

        String contentAsString = result.getResponse().getContentAsString();
        assertTrue(contentAsString.contains("Unknown state: " + unsupportedState));
    }
}
