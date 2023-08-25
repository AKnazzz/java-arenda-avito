package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Getter;
import ru.practicum.shareit.user.model.User;

@Getter
@Builder
public class CommentDto {
    private Long id;
    private String text;
    private User author;
}
