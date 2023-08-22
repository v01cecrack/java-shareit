package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.error.ValidationException;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
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
                                              @RequestParam(defaultValue = "ALL") String state,
                                              @RequestParam(defaultValue = "0") Integer from,
                                              @RequestParam(defaultValue = "20") Integer size) {
        State stateEnum = getState(state);
        return bookingService.getBookingList(stateEnum, userId, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDto> getBookingByItemOwner(@RequestHeader(ItemController.HEADER) int userId,
                                                  @RequestParam(defaultValue = "ALL") String state,
                                                  @RequestParam(defaultValue = "0") Integer from,
                                                  @RequestParam(defaultValue = "20") Integer size) {
        State stateEnum = getState(state);
        return bookingService.getBookingByItemOwner(userId, stateEnum, from, size);
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