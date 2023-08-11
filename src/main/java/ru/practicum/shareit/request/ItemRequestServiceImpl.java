package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.ObjectNotFoundException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDtoItem;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dao.UserRepository;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public ItemRequestDto addItemRequest(int userId, ItemRequestDto itemRequestDto) {
        var user = userRepository.findById(userId).orElseThrow(() -> new ObjectNotFoundException("Такого пользователя не существует!"));
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto);
        itemRequest.setRequester(user);
        itemRequest = itemRequestRepository.save(itemRequest);

        return ItemRequestMapper.toItemRequestDto(itemRequest);
    }


    @Override
    public List<ItemRequestResponseDto> getItemsRequests(int userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException(String.format("Пользователь с id %d не найден", userId)));

        List<ItemRequest> itemRequests = itemRequestRepository.findItemRequestsByRequesterId(userId);
        if (itemRequests.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> requestIds = itemRequests.stream()
                .map(ItemRequest::getId)
                .collect(Collectors.toList());

        Map<Long, List<Item>> itemsByRequest = itemRepository.findItemsByRequestIdIn(requestIds)
                .stream()
                .collect(Collectors.groupingBy(item -> item.getRequest().getId()));

        List<ItemRequestResponseDto> result = itemRequests.stream()
                .map(itemRequest -> {
                    ItemRequestResponseDto itemRequestResponseDto = ItemRequestMapper.toItemRequestResponseDto(itemRequest);

                    List<ItemRequestResponseDtoItem> itemRequestResponseDtoItems = itemsByRequest.getOrDefault(itemRequest.getId(), Collections.emptyList())
                            .stream()
                            .map(ItemRequestMapper::toItemRequestResponseDtoItem)
                            .collect(Collectors.toList());

                    itemRequestResponseDto.setItems(itemRequestResponseDtoItems);
                    return itemRequestResponseDto;
                })
                .collect(Collectors.toList());

        return result;
    }

    @Override
    public List<ItemRequestResponseDto> getAllRequests(int userId, int from, int size) {
        int offset = from > 0 ? from / size : 0;
        PageRequest page = PageRequest.of(offset, size, Sort.by(Sort.Order.desc("created")));

        Page<ItemRequest> itemRequestPage = itemRequestRepository.findByOrderByCreatedDesc(page);

        List<Long> requestIds = itemRequestPage.getContent()
                .stream()
                .map(ItemRequest::getId)
                .collect(Collectors.toList());

        Map<Long, List<Item>> itemsByRequest = itemRepository.findItemsByRequestIdIn(requestIds)
                .stream()
                .collect(Collectors.groupingBy(item -> item.getRequest().getId()));

        List<ItemRequestResponseDto> result = itemRequestPage.getContent()
                .stream()
                .filter(itemRequest -> itemRequest.getRequester().getId() != userId)
                .map(itemRequest -> {
                    ItemRequestResponseDto itemRequestResponseDto = ItemRequestMapper.toItemRequestResponseDto(itemRequest);

                    List<ItemRequestResponseDtoItem> itemRequestResponseDtoItems = itemsByRequest.getOrDefault(itemRequest.getId(), Collections.emptyList())
                            .stream()
                            .map(ItemRequestMapper::toItemRequestResponseDtoItem)
                            .collect(Collectors.toList());

                    itemRequestResponseDto.setItems(itemRequestResponseDtoItems);
                    return itemRequestResponseDto;
                })
                .collect(Collectors.toList());

        return result;
    }


    @Override
    public ItemRequestResponseDto getRequestById(int userId, long requestId) {
        userRepository.findById(userId).orElseThrow(() -> new ObjectNotFoundException("Пользователь с id %d не найден"));
        ItemRequest itemRequest = itemRequestRepository.findById(requestId).orElseThrow(
                () -> new ObjectNotFoundException("Пользователь с id не найден"));

        List<Item> itemList = itemRepository.findItemsByRequestId(requestId);

        ItemRequestResponseDto result = ItemRequestMapper.toItemRequestResponseDto(itemRequest);
        result.setItems(new ArrayList<>());

        for (Item item : itemList) {
            result.getItems().add(ItemRequestMapper.toItemRequestResponseDtoItem(item));
        }

        return result;
    }


}
