package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.NotAvailableException;
import ru.practicum.shareit.error.ObjectNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDao;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private int id = 0;
    private final ItemDao itemDao;
    private final UserDao userDao;

    @Override
    public ItemDto addItem(ItemDto itemDto, Integer userId) {
        validateItem(userId, itemDto);
        itemDto.setId(++id);
        User user = userDao.getUserMap().get(userId);
        itemDao.createItem(itemDto, user);
        return itemDto;
    }


    @Override
    public ItemDto editItem(ItemDto itemDto, Integer userId, Integer itemId) {
        itemDto.setId(itemId);
        return itemDao.updateItem(itemDto, userId);
    }

    @Override
    public ItemDto viewItem(Integer itemId) {
        return itemDao.viewItem(itemId);
    }

    @Override
    public List<ItemDto> getItems(Integer userId) {
        return itemDao.findAll().stream()
                .filter(item -> item.getOwner().getId() == userId)
                .map(item -> ItemMapper.toItemDto(item))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        if (text.isEmpty()) {
            return Collections.emptyList();
        }
        return itemDao.searchItems(text)
                .stream()
                .map(item -> ItemMapper.toItemDto(item))
                .collect(Collectors.toList());
    }

    private void validateItem(Integer userId, ItemDto itemDto) {
        if (!(userDao.getUserMap().containsKey(userId)) ) {
            throw new ObjectNotFoundException();
        }
        if ((itemDto.getAvailable() == false)) {
            throw new NotAvailableException();
        }
    }

}
