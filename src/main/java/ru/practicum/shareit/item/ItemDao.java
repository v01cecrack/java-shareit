package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.error.ObjectNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserDao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@Slf4j
@RequiredArgsConstructor
public class ItemDao {
    private int id = 0;
    private final UserDao userDao;
    private Map<Integer, Item> itemMap = new HashMap<>();

    public Item createItem(Item item) {
        item.setId(++id);
        itemMap.put(item.getId(), item);
        log.info("Вещь: {} добавлена", item.getName());
        return item;
    }

    public Item updateItem(Item item, Integer userId) {
        if (!(itemMap.get(item.getId()).getOwner().getId() == userId)) {
            throw new ObjectNotFoundException("User is not found");
        }
        if (item.getName() == null) {
            item.setName(itemMap.get(item.getId()).getName());
        }
        if (item.getDescription() == null) {
            item.setDescription(itemMap.get(item.getId()).getDescription());
        }
        if (item.getAvailable() == null) {
            item.setAvailable(itemMap.get(item.getId()).getAvailable());
        }
        itemMap.put(item.getId(), item);
        log.info("Вещь: {} обновлена", item.getName());
        return item;
    }

    public ItemDto viewItem(Integer itemId) {
        log.info("Просмотр вещи: {}", itemMap.get(itemId));
        ItemDto itemDto = ItemMapper.toItemDto(itemMap.get(itemId));
        return itemDto;
    }

    public List<Item> findAll() {
        log.info("Текущее количество вещей: {}", itemMap.size());
        return new ArrayList<>(itemMap.values());
    }

    public List<Item> searchItems(String text) {
        log.info("Поиск вещи по '{}'", text);
        return itemMap.values().stream()
                .filter(Item::getAvailable)
                .filter(item -> item.getName().toLowerCase().contains(text.toLowerCase()) ||
                        item.getDescription().toLowerCase().contains(text.toLowerCase()))
                .collect(Collectors.toList());
    }

    public List<ItemDto> getItems(Integer userId) {
        return findAll().stream()
                .filter(item -> item.getOwner().getId() == userId)
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }
}
