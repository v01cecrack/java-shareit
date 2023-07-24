package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.NotAvailableException;
import ru.practicum.shareit.error.ObjectNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDao;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.item.dto.ItemMapper.toItem;
import static ru.practicum.shareit.item.dto.ItemMapper.toItemDto;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemDao itemDao;
    private final UserDao userDao;

    @Override
    public ItemDto addItem(ItemDto itemDto, Integer userId) {
        validateItem(userId, itemDto);
        User user = userDao.getUser(userId);
        Item item = toItem(itemDto, user);
        itemDao.createItem(item);
        itemDto = toItemDto(item);
        return itemDto;
    }

    @Override
    public ItemDto editItem(ItemDto itemDto, Integer userId, Integer itemId) {
        itemDto.setId(itemId);
        Item item = toItem(itemDto, userDao.getUser(userId));
        itemDao.updateItem(item, userId);
        itemDto = toItemDto(item);
        return itemDto;
    }

    @Override
    public ItemDto viewItem(Integer itemId) {
        return itemDao.viewItem(itemId);
    }

    @Override
    public List<ItemDto> getItems(Integer userId) {
        return itemDao.getItems(userId);
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        if (text.isEmpty()) {
            return Collections.emptyList();
        }
        return itemDao.searchItems(text)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    private void validateItem(Integer userId, ItemDto itemDto) {
        if ((userDao.getUser(userId)) == null) {
            throw new ObjectNotFoundException("User is not found");
        }
        if ((!itemDto.getAvailable())) {
            throw new NotAvailableException("Not available item");
        }
    }

}
