package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.ObjectNotFoundException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.user.dao.UserRepository;

import javax.transaction.Transactional;
import java.util.*;
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
        userRepository.findById(userId).orElseThrow(() -> new ObjectNotFoundException(String.format("Пользователь с id %d не найден", userId)));
        var itemRequests = itemRequestRepository.findItemRequestsByRequesterId(userId);
        if (itemRequests.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> requestIds = itemRequests.stream()
                .map(ItemRequest::getId)
                .collect(Collectors.toList());
        List<Item> itemList = itemRepository.findItemsByRequestIdIn(requestIds);

        Map<ItemRequestResponseDto, List<Item>> resultMap = new HashMap<>();

        for (ItemRequestResponseDto dto : itemRequests.stream()
                .map(ItemRequestMapper::toItemRequestResponseDto)
                .collect(Collectors.toList())) {
            resultMap.put(dto, new ArrayList<>());
        }

        return getItemRequestResponseDtos(itemList, resultMap);
    }

    private List<ItemRequestResponseDto> getItemRequestResponseDtos(List<Item> itemList, Map<ItemRequestResponseDto, List<Item>> resultMap) {
        for (Item item : itemList) {
            ItemRequestResponseDto dto = resultMap.keySet().stream()
                    .filter(itemRequestResponseDto ->
                            Objects.equals(item.getRequest().getId(), itemRequestResponseDto.getId()))
                    .findFirst().orElse(null);

            if (dto != null) {
                resultMap.get(dto).add(item);
            }
        }

        resultMap.forEach((dto, items) -> dto.setItems(
                items.stream()
                        .map(ItemRequestMapper::toItemRequestResponseDtoItem)
                        .collect(Collectors.toList())
        ));

        return new ArrayList<>(resultMap.keySet());
    }

    private List<ItemRequestResponseDto> getItemRequestResponseDtos(List<Item> itemList, List<ItemRequestResponseDto> result) {
        for (ItemRequestResponseDto itemRequestResponseDto : result) {
            if (itemRequestResponseDto.getItems() == null) {
                itemRequestResponseDto.setItems(new ArrayList<>());
            }
            for (Item item : itemList) {
                if (Objects.equals(item.getRequest().getId(), itemRequestResponseDto.getId())) {

                    itemRequestResponseDto.getItems().add(ItemRequestMapper.toItemRequestResponseDtoItem(item));

                }
            }
        }

        return result;
    }

    @Override
    public List<ItemRequestResponseDto> getAllRequests(int userId, int from, int size) {
        int offset = from > 0 ? from / size : 0;
        PageRequest page = PageRequest.of(offset, size);

        Page<ItemRequest> itemRequestPage = itemRequestRepository.findByOrderByCreatedDesc(page);

        List<Long> requestIds = itemRequestPage.getContent().stream()
                .map(ItemRequest::getId)
                .collect(Collectors.toList());

        List<Item> itemList = itemRepository.findItemsByRequestIdIn(requestIds);

        Map<ItemRequestResponseDto, List<Item>> resultMap = new HashMap<>();

        for (ItemRequestResponseDto dto : itemRequestPage.getContent().stream()
                .map(ItemRequestMapper::toItemRequestResponseDto)
                .filter(item -> item.getId() != userId)
                .collect(Collectors.toList())) {
            resultMap.put(dto, new ArrayList<>());
        }

        return getItemRequestResponseDtos(itemList, resultMap);
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
