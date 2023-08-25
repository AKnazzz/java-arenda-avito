package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    private static final String USER_NOT_FOUND_ERROR = "Нет такого User.";

    @Override
    @Transactional
    public ItemRequestDto create(ItemRequestDto itemRequestDto, Long userId) {
        ItemRequest itemRequest = ItemRequestMapper.dtoToItemRequest(itemRequestDto);
        itemRequest.setRequestor(userRepository.findById(userId).orElseThrow(() -> {
            log.info(USER_NOT_FOUND_ERROR);
            return new EntityNotFoundException(USER_NOT_FOUND_ERROR);
        }));
        itemRequest.setCreated(LocalDateTime.now());
        log.info("itemRequestDto {} сохранён.", itemRequestDto);
        itemRequestRepository.save(itemRequest);
        return ItemRequestMapper.itemRequestToDto(itemRequest);
    }

    @Override
    public List<ItemRequestResponseDto> getAllForRequestor(Long userId) {
        userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND_ERROR));

        if (itemRequestRepository.findAllByRequestor_idOrderByCreatedAsc(userId).isEmpty()) {
            log.info("Получен пустой список ItemRequest для User c ID {} - у него нет запросов.", userId);
            return List.of();
        }

        List<ItemRequestResponseDto> itemRequestResponseDtos
                = itemRequestRepository.findAllByRequestor_idOrderByCreatedAsc(userId)
                .stream()
                .map(a -> ItemRequestResponseDto.create(a, ItemMapper.listItemsToListResponseDto(
                        itemRepository.findAllByRequestIdOrderByIdAsc(a.getRequestor().getId()))))
                .collect(Collectors.toList());

        log.info("Получен список ItemRequest вместе с данными об ответах на них для User c ID {}.", userId);
        return itemRequestResponseDtos;
    }

    @Override
    public List<ItemRequestResponseDto> getAllRequests(int from, int size, long userId) {
        userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND_ERROR));

        Pageable pageable = PageRequest.of(from, size, Sort.by(Sort.Direction.DESC, "created"));

        List<ItemRequestResponseDto> itemResponseDtos = (itemRequestRepository.findAllByRequestor_IdNotIn(
                List.of(userId), pageable).getContent()).stream()
                .map(a -> ItemRequestResponseDto.create(a, ItemMapper.listItemsToListResponseDto(
                        itemRepository.findAllByRequestIdOrderByIdAsc(a.getId()))))
                .collect(Collectors.toList());
        log.info("Получен полный список ItemRequest по запросу от User c ID {}.", userId);
        return itemResponseDtos;
    }

    @Override
    public ItemRequestResponseDto getById(Long requestId, Long userId) {
        userRepository.findById(userId).orElseThrow(() -> {
            log.info(USER_NOT_FOUND_ERROR);
            return new EntityNotFoundException(USER_NOT_FOUND_ERROR);
        });
        ItemRequest itemRequest = itemRequestRepository.findById(requestId).orElseThrow(() -> {
            log.info(USER_NOT_FOUND_ERROR);
            return new EntityNotFoundException(USER_NOT_FOUND_ERROR);
        });
        List<ItemResponseDto> items = ItemMapper.listItemsToListResponseDto(
                itemRepository.findAllByRequestIdOrderByIdAsc(requestId));

        log.info("Получен ItemRequest с ID {} по запросу от User c ID {}.", requestId, userId);
        return ItemRequestResponseDto.create(itemRequest, items);
    }


}
