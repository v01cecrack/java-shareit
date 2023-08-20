package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@Builder
public class CommentDto {
    private long id;
    @NotEmpty
    private String text;
    private long itemId;
    private long authorId;
    private String authorName;
}
