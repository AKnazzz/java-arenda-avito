package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.model.Comment;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class CommentMapper {

    private CommentMapper() {
    }

    public static CommentDto commentToDto(Comment comment) {
        if (comment == null) {
            throw new IllegalArgumentException("Comment can not be null.");
        }

        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .author(comment.getAuthor())
                .build();
    }

    public static CommentResponseDto toResponseDto(Comment comment) {
        if (comment == null) {
            throw new IllegalArgumentException("Comment can not be null.");
        }

        return CommentResponseDto.builder()
                .id(comment.getId())
                .authorName(comment.getAuthor().getName())
                .text(comment.getText())
                .created(comment.getCreated())
                .build();
    }

    public static Comment dtoToComment(CommentDto commentDto) {
        if (commentDto == null) {
            throw new IllegalArgumentException("CommentDto can not be null.");
        }

        return Comment.builder()
                .id(commentDto.getId())
                .text(commentDto.getText())
                .author(commentDto.getAuthor())
                .build();
    }

    public static List<CommentDto> listCommentsToListDto(Collection<Comment> comments) {
        return comments.stream().map(CommentMapper::commentToDto).collect(Collectors.toList());
    }

    public static List<CommentResponseDto> listCommentsToListResponse(Collection<Comment> comments) {
        return comments.stream().map(CommentMapper::toResponseDto).collect(Collectors.toList());
    }

}
