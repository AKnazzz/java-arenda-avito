package ru.practicum.shareit.request.mapper;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class ItemRequestMapper {

    private ItemRequestMapper() {
    }

    public static ItemRequestDto itemRequestToDto(ItemRequest itemRequest) {
        if (itemRequest == null) {
            throw new IllegalArgumentException("ItemRequest can not be null.");
        }

        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .build();
    }

    public static ItemRequest dtoToItemRequest(ItemRequestDto itemRequestDto) {
        if (itemRequestDto == null) {
            throw new IllegalArgumentException("ItemRequestDto can not be null.");
        }

        return ItemRequest.builder()
                .id(itemRequestDto.getId())
                .description(itemRequestDto.getDescription())
                .created(itemRequestDto.getCreated())
                .build();
    }

    public static ItemRequestResponseDto toItemRequestResponse(ItemRequest itemRequest) {
        if (itemRequest == null) {
            throw new IllegalArgumentException("ItemRequest can not be null.");
        }

        return ItemRequestResponseDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .build();
    }

    public static List<ItemRequestDto> listItemRequestToDto(Collection<ItemRequest> itemRequests) {
        return itemRequests.stream().map(ItemRequestMapper::itemRequestToDto).collect(Collectors.toList());
    }

}
