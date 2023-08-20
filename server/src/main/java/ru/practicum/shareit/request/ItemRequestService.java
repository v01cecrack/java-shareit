package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto addItemRequest(int userId, ItemRequestDto itemRequestDto);

    List<ItemRequestResponseDto> getItemsRequests(int userId);

    List<ItemRequestResponseDto> getAllRequests(int userId, int from, int size);

    ItemRequestResponseDto getRequestById(int userId, long requestId);
}
