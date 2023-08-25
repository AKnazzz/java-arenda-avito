package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;


@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody ItemRequestDto itemRequestDto,
            @RequestHeader(value = "X-Sharer-User-Id") @Positive Long userId) {
        log.info("Получен POST запрос по эндпоинту /requests на добавление нового ItemRequest {} от User с ID {}.",
                itemRequestDto, userId);
        return itemRequestClient.create(itemRequestDto, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllForRequestor(
            @RequestHeader(value = "X-Sharer-User-Id") @Positive Long userId) {
        log.info("Получен GET запрос по эндпоинту /requests на получение всех ItemRequest с данными об ответах "
                + "на них для User с ID {}.", userId);
        return itemRequestClient.getAllForRequestor(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAll(
            @RequestParam(defaultValue = "0", required = false) @PositiveOrZero int from,
            @RequestParam(defaultValue = "20", required = false) @Positive int size,
            @RequestHeader(value = "X-Sharer-User-Id") @Positive Long userId) {
        log.info("Получен GET запрос по эндпоинту /requests/all на получение всех ItemRequest для User с ID {}.",
                userId);
        return itemRequestClient.getAll(from, size, userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getById(@PathVariable @Positive long requestId,
            @RequestHeader(value = "X-Sharer-User-Id") @Positive Long userId) {
        log.info("Получен GET запрос по эндпоинту /requests/{} на получение ItemRequest c ID {} для User с ID {}.",
                requestId, requestId, userId);
        return itemRequestClient.getById(requestId, userId);
    }

}
