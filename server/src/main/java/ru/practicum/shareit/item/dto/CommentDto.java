package ru.practicum.shareit.item.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentDto {
    private long id;
    private String text;
    private long itemId;
    private long authorId;
    private String authorName;
    private LocalDateTime created;
}
