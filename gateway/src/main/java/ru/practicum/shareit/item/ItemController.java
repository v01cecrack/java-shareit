package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Controller
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    public static final String HEADER = "X-Sharer-User-Id";
    private final ItemClient itemClient;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> addItem(@Valid @RequestBody ItemDto itemDto, @RequestHeader(HEADER) Integer id) {
        log.info("Получен POST-запрос: /items на добавления item:{}", itemDto.getName());
        return itemClient.addItem(id, itemDto);
    }

    @PatchMapping(value = "/{itemId}")
    public ResponseEntity<Object> editItem(@RequestBody ItemDto itemDto, @RequestHeader(HEADER) Integer userId, @PathVariable Integer itemId) {
        log.info("Получен PATCH-запрос:/items/search на обновление вещи по ID = {}", itemId);
        itemDto.setId(itemId);
        return itemClient.updateItem(userId, itemDto);
    }

    @GetMapping(value = "/{itemId}")
    public ResponseEntity<Object> viewItem(@PathVariable Integer itemId, @RequestHeader(HEADER) Integer userId) {
        log.info("Получен GET-запрос:/items/{itemId} на получение вещи по id = {}", itemId);
        return itemClient.getItem(itemId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getItems(@RequestHeader(HEADER) int userId,
                                           @RequestParam(defaultValue = "0") @Min(0) int from,
                                           @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {
        log.info("Получен GET-запрос:/items на получения списка всех вещей");
        return itemClient.getItems(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestParam(value = "text") String text,
                                              @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                              @RequestParam(defaultValue = "20") @Min(1) @Max(100) Integer size) {
        log.info("Получен GET-запрос:/items/search на поиск вещи, название или описание которой, содержит слово {}", text);
        return itemClient.searchItems(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader(HEADER) int userId, @Valid @RequestBody CommentDto commentDto,
                                             @PathVariable int itemId) {
        return itemClient.addComment(userId, commentDto, itemId);
    }
}
