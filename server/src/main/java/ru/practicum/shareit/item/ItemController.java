package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@Slf4j
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    public static final String HEADER = "X-Sharer-User-Id";
    private final ItemService itemService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto addItem(@RequestBody ItemDto itemDto, @RequestHeader(HEADER) Integer id) {
        log.info("Получен POST-запрос: /items на добавления item:{}", itemDto.getName());
        return itemService.addItem(itemDto, id);
    }

    @PatchMapping(value = "/{itemId}")
    public ItemDto editItem(@RequestBody ItemDto itemDto, @RequestHeader(HEADER) Integer userId, @PathVariable Integer itemId) {
        log.info("Получен PATCH-запрос:/items/search на обновление вещи по ID = {}", itemId);
        itemDto.setId(itemId);
        return itemService.editItem(itemDto, userId);
    }

    @GetMapping(value = "/{itemId}")
    public ItemDto viewItem(@PathVariable Integer itemId, @RequestHeader(HEADER) Integer userId) {
        log.info("Получен GET-запрос:/items/{itemId} на получение вещи по id = {}", itemId);
        return itemService.viewItem(itemId, userId);
    }

    @GetMapping
    public List<ItemDto> getItems(@RequestHeader(HEADER) int userId,
                                  @RequestParam(defaultValue = "0") int from,
                                  @RequestParam(defaultValue = "20") int size) {
        log.info("Получен GET-запрос:/items на получения списка всех вещей");
        return itemService.getItems(userId, from, size);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam(value = "text") String text,
                                     @RequestParam(defaultValue = "0") Integer from,
                                     @RequestParam(defaultValue = "20") Integer size) {
        log.info("Получен GET-запрос:/items/search на поиск вещи, название или описание которой, содержит слово {}", text);
        return itemService.searchItems(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader(HEADER) int userId, @Valid @RequestBody CommentDto commentDto,
                                 @PathVariable int itemId) {
        return itemService.addComment(userId, itemId, commentDto);
    }

}
