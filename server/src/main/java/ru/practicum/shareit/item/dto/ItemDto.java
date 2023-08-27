package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.user.User;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */

@Getter
@Setter
@EqualsAndHashCode
@Builder
public class ItemDto {
    private int id;
    private String name;
    private String description;
    private Boolean available;
    private String request;
    private BookingDto lastBooking;
    private BookingDto nextBooking;
    private User owner;
    private List<CommentDto> comments;
    private long requestId;
}

