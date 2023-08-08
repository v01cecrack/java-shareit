package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.user.User;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-item-requests.
 */

@Getter
@EqualsAndHashCode
@Setter
@Builder
public class ItemRequestDto {
    private Long id;
    @NotEmpty
    private final String description;
    private User requestor;
    private LocalDateTime created;

}
