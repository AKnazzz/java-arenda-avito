package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {

    Item create(Item item, Long userOwnerId);

    Item update(Long id, Item item, Long userOwnerId);

    Item getById(Long id, Long userId);

    List<Item> getAllByOwner(Long userOwnerId);

    void deleteById(Long id, Long userOwnerId);

    List<Item> search(String text, Long userId);

}
