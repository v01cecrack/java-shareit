package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;

import static ru.practicum.shareit.item.ItemController.HEADER;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemRequestDto addRequest(@RequestHeader(HEADER) int userId,
                                     @Valid @RequestBody ItemRequestDto itemRequestDto) {
        return itemRequestService.addItemRequest(userId, itemRequestDto);
    }

    @GetMapping
    public List<ItemRequestResponseDto> getItemsByUserId(@RequestHeader(HEADER) int userId) {
        return itemRequestService.getItemsRequests(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestResponseDto> returnAll(@RequestHeader(HEADER) int userId,
                                                  @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                                  @RequestParam(defaultValue = "20") @Min(1) @Max(100) Integer size) {
        var result = itemRequestService.getAllRequests(userId, from, size);
        return result;
    }

    @GetMapping("/{requestId}")
    public ItemRequestResponseDto get(@RequestHeader(HEADER) int userId,
                                      @PathVariable long requestId) {
        return itemRequestService.getRequestById(userId, requestId);
    }
}
