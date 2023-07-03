package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
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
    private static final String HEADER = "X-Sharer-User-Id";
    private final ItemService itemService;

    @PostMapping
    public ItemDto addItem(@Valid @RequestBody ItemDto itemDto, @RequestHeader(HEADER) Integer id) {
        log.info("Получен POST-запрос: /items на добавления item:{}", itemDto.getName());
        return itemService.addItem(itemDto, id);
    }

    @PatchMapping(value = "/{itemId}")
    public ItemDto editItem(@RequestBody ItemDto itemDto, @RequestHeader(HEADER) Integer userId, @PathVariable Integer itemId) {
        log.info("Получен PATCH-запрос:/items/search на обновление вещи по ID = {}", itemId);
        return itemService.editItem(itemDto, userId, itemId);
    }

    @GetMapping(value = "/{itemId}")
    public ItemDto viewItem(@PathVariable Integer itemId) {
        log.info("Получен GET-запрос:/items/{itemId} на получение вещи по id = {}", itemId);
        return itemService.viewItem(itemId);
    }

    @GetMapping
    public List<ItemDto> getItems(@RequestHeader(HEADER) Integer userId) {
        log.info("Получен GET-запрос:/items на получения списка всех вещей");
        return itemService.getItems(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam String text) {
        log.info("Получен GET-запрос:/items/search на поиск вещи, название или описание которой, содержит слово {}", text);
        return itemService.searchItems(text);
    }

}
