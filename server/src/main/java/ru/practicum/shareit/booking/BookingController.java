package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.error.ValidationException;
import ru.practicum.shareit.item.ItemController;

import java.util.List;


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
    @ResponseStatus(HttpStatus.CREATED)
    public BookingDto addBookingRequest(@RequestBody BookingDto bookingDto, @RequestHeader(ItemController.HEADER) Integer userId) {
        log.info("Добавлен новый запрос: {}", bookingDto);
        return bookingService.createBooking(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approveBooking(@PathVariable Integer bookingId, @RequestHeader(ItemController.HEADER) Integer userId,
                                     @RequestParam(name = "approved") boolean response) {
        log.info("Отправлен запрос на изменение статуса бронирования от владельца c id: {}", userId);
        return bookingService.approveBooking(bookingId, userId, response);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBooking(@PathVariable Integer bookingId, @RequestHeader(ItemController.HEADER) Integer userId) {
        return bookingService.getBooking(bookingId, userId);
    }

    @GetMapping
    public List<BookingDto> getBookingsOfUser(@RequestHeader(ItemController.HEADER) int userId,
                                              @RequestParam(defaultValue = "ALL") State state,
                                              @RequestParam(defaultValue = "0") Integer from,
                                              @RequestParam(defaultValue = "10") Integer size) {
//        State stateEnum = getState(state);
        return bookingService.getBookingList(state, userId, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDto> getBookingByItemOwner(@RequestHeader(ItemController.HEADER) int userId,
                                                  @RequestParam(defaultValue = "ALL") State state,
                                                  @RequestParam(defaultValue = "0") Integer from,
                                                  @RequestParam(defaultValue = "10") Integer size) {
//        State stateEnum = getState(state);
        return bookingService.getBookingByItemOwner(userId, state, from, size);
    }

    private State getState(String state) {
        State stateEnum;
        try {
            stateEnum = State.valueOf(state);

        } catch (Exception ex) {
            throw new ValidationException("UnknownNNN state: UNSUPPORTED_STATUS");
        }
        return stateEnum;
    }
}
