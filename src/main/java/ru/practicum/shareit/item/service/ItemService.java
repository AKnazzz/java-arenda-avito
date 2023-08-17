package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;

import java.util.List;

public interface ItemService {

    ItemDto create(ItemDto itemDto, Long userOwnerId);

    ItemDto update(Long id, ItemDto itemDto, Long userOwnerId);

    ItemResponseDto getById(Long id, Long userId);

    List<ItemResponseDto> getAllByOwner(int from, int size, Long userOwnerId);

    void deleteById(Long id, Long userOwnerId);

    List<ItemDto> search(int from, int size, String text, Long userId);

    CommentResponseDto addComment(CommentDto commentDto, long itemId, long userId);

}
