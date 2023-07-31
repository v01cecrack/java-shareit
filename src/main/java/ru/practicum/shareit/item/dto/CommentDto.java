package ru.practicum.shareit.item.dto;

import lombok.*;

import javax.validation.ValidationException;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentDto {
    private long id;
    @NotEmpty
    private String text;
    private long itemId;
    private long authorId;
    private String authorName;
    private LocalDateTime created;
}
