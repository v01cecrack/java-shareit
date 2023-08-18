package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
@Builder
public class ItemRequestResponseDto {
    private Long id;
    private final String description;
    private List<ItemRequestResponseDtoItem> items;
    private LocalDateTime created;
}
