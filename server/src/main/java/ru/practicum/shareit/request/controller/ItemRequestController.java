package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping
    public ResponseEntity<ItemRequestDto> create(@RequestBody ItemRequestDto itemRequestDto,
            @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        log.info("Получен POST запрос по эндпоинту /requests на добавление нового ItemRequest {} от User с ID {}.",
                itemRequestDto, userId);
        return new ResponseEntity<>(itemRequestService.create(itemRequestDto, userId), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ItemRequestResponseDto>> getAllForRequestor(
            @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        log.info("Получен GET запрос по эндпоинту /requests на получение всех ItemRequest с данными об ответах "
                + "на них для User с ID {}.", userId);
        return new ResponseEntity<>(itemRequestService.getAllForRequestor(userId), HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<ItemRequestResponseDto>> getAll(
            @RequestParam(defaultValue = "0", required = false) int from,
            @RequestParam(defaultValue = "20", required = false) int size,
            @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        log.info("Получен GET запрос по эндпоинту /requests/all на получение всех ItemRequest для User с ID {}.",
                userId);
        return new ResponseEntity<>(itemRequestService.getAllRequests(from, size, userId),
                HttpStatus.OK);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<ItemRequestResponseDto> getById(@PathVariable long requestId,
            @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        log.info("Получен GET запрос по эндпоинту /requests/{} на получение ItemRequest c ID {} для User с ID {}.",
                requestId, requestId, userId);
        return new ResponseEntity<>(itemRequestService.getById(requestId, userId), HttpStatus.OK);
    }
}
