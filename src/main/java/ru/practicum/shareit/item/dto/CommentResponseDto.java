package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Getter
@Builder
public class CommentResponseDto {
    private Long id;
    @NotBlank
    private String text;
    private String authorName;
    private LocalDateTime created = LocalDateTime.now();
}
