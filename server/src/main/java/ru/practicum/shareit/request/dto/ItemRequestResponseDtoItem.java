package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
@Builder
public class ItemRequestResponseDtoItem {
    private long id;
    private String name;
    private String description;
    private long requestId;
    private boolean available;
}
