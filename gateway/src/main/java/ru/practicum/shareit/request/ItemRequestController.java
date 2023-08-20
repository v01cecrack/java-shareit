package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import static ru.practicum.shareit.item.ItemController.HEADER;

@Controller
@RequestMapping("/requests")
@Slf4j
@RequiredArgsConstructor
@Validated
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> addRequest(@RequestHeader(HEADER) int userId,
                                             @Valid @RequestBody ItemRequestDto itemRequestDto) {
        return itemRequestClient.addItemRequest(userId, itemRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getItemsByUserId(@RequestHeader(HEADER) int userId) {
        return itemRequestClient.getItemsRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> returnAll(@RequestHeader(HEADER) int userId,
                                            @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                            @RequestParam(defaultValue = "20") @Min(1) @Max(100) Integer size) {
        var result = itemRequestClient.getAllRequests(userId, from, size);
        return result;
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> get(@RequestHeader(HEADER) int userId,
                                      @PathVariable long requestId) {
        return itemRequestClient.getRequestById(userId, requestId);
    }
}
