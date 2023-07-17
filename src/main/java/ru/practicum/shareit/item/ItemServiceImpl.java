package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.NotAvailableException;
import ru.practicum.shareit.error.ObjectNotFoundException;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.dao.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.item.dto.ItemMapper.toItem;
import static ru.practicum.shareit.item.dto.ItemMapper.toItemDto;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

//    private final ItemDao itemDao;
//    private final UserDao userDao;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public ItemDto addItem(ItemDto itemDto, Integer userId) {
        validateItem(userId, itemDto);
        User user = userRepository.findById(userId).get();
//        User user = userDao.getUser(userId);
        Item item = toItem(itemDto, user);
        itemRepository.save(item);
//        itemDao.createItem(item);
        itemDto = toItemDto(item);
        return itemDto;
    }

    @Override
    public ItemDto editItem(ItemDto itemDto, Integer userId, Integer itemId) {
        itemDto.setId(itemId);
        Item oldItem = itemRepository.findById(itemId).get();
        if (!(oldItem.getOwner().getId() == userId)) {
            throw new ObjectNotFoundException("User is not found");
        }
        if (itemDto.getName() == null) {
            itemDto.setName(oldItem.getName());
        }
        if (itemDto.getDescription() == null) {
            itemDto.setDescription(oldItem.getDescription());
        }
        if (itemDto.getAvailable() == null) {
            itemDto.setAvailable(oldItem.getAvailable());
        }
        Item item = toItem(itemDto, userRepository.findById(userId).get());
        itemRepository.save(item);
//        itemDao.updateItem(item, userId);
        //itemDto = toItemDto(item);
        return itemDto;
    }

    @Override
    public ItemDto viewItem(Integer itemId) {
//        return itemDao.viewItem(itemId);
        if (itemRepository.findById(itemId).isEmpty()) {
            throw new ObjectNotFoundException("item is not found");
        }
        return ItemMapper.toItemDto(itemRepository.findById(itemId).get());
    }

    @Override
    public List<ItemDto> getItems(Integer userId) {
//        return itemDao.getItems(userId);
        List<Item> items = itemRepository.findItemByOwnerId(userId);
        return items.stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        if (text.isEmpty()) {
            return Collections.emptyList();
        }
//        return itemDao.searchItems(text)
//                .stream()
//                .map(ItemMapper::toItemDto)
//                .collect(Collectors.toList());
        return itemRepository.search(text).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    private void validateItem(Integer userId, ItemDto itemDto) {
        if (userRepository.findById(userId).isEmpty()) {
//        if ((userDao.getUser(userId)) == null) {
            throw new ObjectNotFoundException("User is not found");
        }
        if ((!itemDto.getAvailable())) {
            throw new NotAvailableException("Not available item");
        }
    }

}
