package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {
    BookingDto createBooking(BookingDto bookingDto, Integer userId);

    BookingDto getBooking(Integer bookingId, Integer userId);

    BookingDto approveBooking(Integer bookingId, Integer userId, boolean response);

    List<BookingDto> getBookingList(State state, Integer userId, int from, int size);

    List<BookingDto> getBookingByItemOwner(Integer userId, State state, int from, int size);
}
