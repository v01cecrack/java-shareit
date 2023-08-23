package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.item.ItemController;

import javax.validation.Valid;
import javax.validation.ValidationException;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @GetMapping
    public ResponseEntity<Object> getBookings(@RequestHeader("X-Sharer-User-Id") int userId,
                                              @RequestParam(name = "state", defaultValue = "ALL") String state,
                                              @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                              @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {

        BookingState stateEnum = getState(state);
//        BookingState state = BookingState.from();
//                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
//        log.info("Get booking with state {}, userId={}, from={}, size={}", state, userId, from, size);
        return bookingClient.getBookings(userId, stateEnum, from, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> bookItem(@RequestHeader("X-Sharer-User-Id") int userId,
                                           @RequestBody @Valid BookItemRequestDto requestDto) {
        log.info("Creating booking {}, userId={}", requestDto, userId);
        return bookingClient.bookItem(userId, requestDto);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader("X-Sharer-User-Id") int userId,
                                             @PathVariable Long bookingId) {
        log.info("Get booking {}, userId={}", bookingId, userId);
        return bookingClient.getBooking(userId, bookingId);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approveBooking(@PathVariable Integer bookingId, @RequestHeader(ItemController.HEADER) Integer userId,
                                                 @RequestParam(name = "approved") boolean response) {
        log.info("Отправлен запрос на изменение статуса бронирования от владельца c id: {}", userId);
        return bookingClient.approveBooking(bookingId, userId, response);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingByItemOwner(@RequestHeader(ItemController.HEADER) int userId,
                                                        @RequestParam(defaultValue = "ALL") String state,
                                                        @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                                        @RequestParam(defaultValue = "20") @Positive Integer size) {
//        if (stateParam.equalsIgnoreCase("unsupported_status")) {
//            throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
//        }
//        BookingState state = BookingState.from(stateParam);
//                .orElseThrow(() -> new IllegalArgumentException("UnknowWWWWn state: " + stateParam));
        BookingState stateEnum = getState(state);
        return bookingClient.getBookingByItemOwner(userId, stateEnum, from, size);
    }

    private BookingState getState(String state) {
        BookingState stateEnum;
        try {
            stateEnum = BookingState.valueOf(state);

        } catch (Exception ex) {
            throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
        }
        return stateEnum;
    }

}
