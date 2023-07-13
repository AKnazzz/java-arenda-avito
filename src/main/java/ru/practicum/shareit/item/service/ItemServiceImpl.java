package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;

import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @Override
    public ItemDto create(ItemDto itemDto, Long userOwnerId) {
        return ItemMapper.itemToDto(itemRepository.create(ItemMapper.dtoToItem(itemDto), userOwnerId));
    }

    @Override
    public ItemDto update(Long id, ItemDto itemDto, Long userOwnerId) {
        return ItemMapper.itemToDto(itemRepository.update(id, ItemMapper.dtoToItem(itemDto), userOwnerId));
    }

    @Override
    public ItemDto getById(Long id, Long userId) {
        return ItemMapper.itemToDto(itemRepository.getById(id, userId));
    }

    @Override
    public List<ItemDto> getAllByOwner(Long userOwnerId) {
        return ItemMapper.listItemsToListDto(itemRepository.getAllByOwner(userOwnerId));
    }

    @Override
    public void deleteById(Long id, Long userOwnerId) {
        itemRepository.deleteById(id, userOwnerId);
    }

    @Override
    public List<ItemDto> search(String text, Long userId) {
        return ItemMapper.listItemsToListDto(itemRepository.search(text, userId));
    }

}
