package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto create(ItemRequestDto itemRequestDto, Long userId);

    List<ItemRequestResponseDto> getAllForRequestor(Long userId);

    List<ItemRequestResponseDto> getAllRequests(int from, int size, long userId);

    ItemRequestResponseDto getById(Long requestId, Long userId);

}
