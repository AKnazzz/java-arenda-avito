package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Getter;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotBlank;

@Getter
@Builder
public class CommentDto {
    private Long id;
    @NotBlank
    private String text;
    private User author;
}
