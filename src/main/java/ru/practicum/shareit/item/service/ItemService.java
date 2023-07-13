package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto create(ItemDto itemDto, Long userOwnerId);

    ItemDto update(Long id, ItemDto itemDto, Long userOwnerId);

    ItemDto getById(Long id, Long userId);

    List<ItemDto> getAllByOwner(Long userOwnerId);

    void deleteById(Long id, Long userOwnerId);

    List<ItemDto> search(String text, Long userId);


}
