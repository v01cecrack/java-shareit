package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.error.ValidationException;

import javax.validation.Valid;
import java.util.List;

import static ru.practicum.shareit.item.ItemController.HEADER;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@Slf4j
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDto addBookingRequest(@Valid @RequestBody BookingDto bookingDto, @RequestHeader(HEADER) Integer userId) {
        log.info("Добавлен новый запрос: {}", bookingDto);
        return bookingService.createBooking(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approveBooking(@PathVariable Integer bookingId, @RequestHeader(HEADER) Integer userId,
                                     @RequestParam(name = "approved") boolean response) {
        log.info("Отправлен запрос на изменение статуса бронирования от владельца c id: {}", userId);
        return bookingService.approveBooking(bookingId, userId, response);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBooking(@PathVariable Integer bookingId, @RequestHeader(HEADER) Integer userId) {
        return bookingService.getBooking(bookingId, userId);
    }

    @GetMapping
    public List<BookingDto> getBookingList(@RequestParam(defaultValue = "ALL") String state, @RequestHeader(HEADER) Integer userId) {
        State stateEnum = getState(state);
        return bookingService.getBookingList(stateEnum, userId);
    }

    @GetMapping("/owner")
    public List<BookingDto> getBookingByItemOwner(@RequestParam(defaultValue = "ALL") String state, @RequestHeader(HEADER) Integer userId) {
        State stateEnum = getState(state);
        return bookingService.getBookingByItemOwner(userId, stateEnum);
    }

    private State getState(String state) {
        State stateEnum;
        try {
            stateEnum = State.valueOf(state);

        } catch (Exception ex) {
            throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
        }
        return stateEnum;
    }
}
