package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto addItem(ItemDto itemDto, Integer userId);

    ItemDto editItem(ItemDto itemDto, Integer userId, Integer itemId);

    ItemDto viewItem(Integer itemId, Integer userId);

    List<ItemDto> getItems(Integer userId);

    List<ItemDto> searchItems(String text);

    CommentDto addComment(int userId, int itemId, CommentDto commentDto);
}
