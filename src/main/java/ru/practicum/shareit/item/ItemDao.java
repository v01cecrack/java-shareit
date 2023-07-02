package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.error.ObjectNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ru.practicum.shareit.item.dto.ItemMapper.toItem;

@Repository
@Slf4j
@RequiredArgsConstructor
public class ItemDao {
    private final UserDao userDao;
    private Map<Integer, Item> itemMap = new HashMap<>();

    public ItemDto createItem(ItemDto itemDto, User user) {
        Item item = toItem(itemDto, user);
        itemMap.put(item.getId(), item);
        log.info("Вещь: {} добавлена", item.getName());
        return itemDto;
    }

    public ItemDto updateItem(ItemDto itemDto, Integer userId) {
        if (!(itemMap.get(itemDto.getId()).getOwner().getId() == userId)) {
            throw new ObjectNotFoundException();
        }
        if (itemDto.getName() == null) {
            itemDto.setName(itemMap.get(itemDto.getId()).getName());
        }
        if (itemDto.getDescription() == null) {
            itemDto.setDescription(itemMap.get(itemDto.getId()).getDescription());
        }
        if (itemDto.getAvailable() == null) {
            itemDto.setAvailable(itemMap.get(itemDto.getId()).getAvailable());
        }
        Item item = ItemMapper.toItem(itemDto, userDao.getUser(userId));
        itemMap.remove(item.getId());
        itemMap.put(item.getId(), item);
        log.info("Вещь: {} обновлена", item.getName());
        return itemDto;
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
}
