package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;


@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ResponseEntity<ItemDto> itemCreate(@Valid @RequestBody ItemDto itemDto,
            @RequestHeader(value = "X-Sharer-User-Id") Long userOwnerId) {
        log.info("Получен POST запрос по эндпоинту /items от User(Owner) c ID {} на добавление Item {}.", userOwnerId,
                itemDto);
        return new ResponseEntity<>(itemService.create(itemDto, userOwnerId), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemResponseDto> getById(@PathVariable @Positive Long id,
            @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        log.info("Получен GET запрос по эндпоинту /items/{} от User c ID {} на получение Item с ID {}.", id, userId,
                id);
        return new ResponseEntity<>(itemService.getById(id, userId), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<ItemResponseDto>> geAllItemsByOwner(
            @RequestHeader(value = "X-Sharer-User-Id") Long userOwnerId) {
        log.info("Получен GET запрос по эндпоинту /items от User(Owner) c ID {} на получение списка всех своих Items.",
                userOwnerId);
        return new ResponseEntity<>(itemService.getAllByOwner(userOwnerId), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteItem(@PathVariable @Positive Long id,
            @RequestHeader(value = "X-Sharer-User-Id") Long userOwnerId) {
        log.info("Получен DELETE запрос по эндпоинту /items/{} от User(Owner) c ID {} на удаление Item с ID {}.", id,
                userOwnerId, id);
        itemService.deleteById(id, userOwnerId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateItem(@PathVariable @Positive Long id, @RequestBody ItemDto itemDto,
            @RequestHeader(value = "X-Sharer-User-Id") Long userOwnerId) {
        log.info(
                "Получен PATCH запрос по эндпоинту /items/{} от User(Owner) c ID {} на обновление данных Item с ID {}.",
                id, userOwnerId, id);
        return new ResponseEntity<>(itemService.update(id, itemDto, userOwnerId), HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDto>> searchItem(@RequestParam(name = "text") String text1,
            @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        log.info(
                "Получен GET запрос по эндпоинту /items/search от User c ID {} на получение списка Item по запросу '{}'.",
                userId, text1);
        return new ResponseEntity<>(itemService.search(text1, userId), HttpStatus.OK);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<CommentResponseDto> addComment(@PathVariable("itemId") @Positive long itemId,
            @RequestHeader(value = "X-Sharer-User-Id") @Positive Long userId,
            @Valid @RequestBody CommentDto commentDto) {
        log.info("Получен POST запрос по эндпоинту /items/{}/comment от User c ID {} на создание Comment {}.", itemId,
                userId, commentDto);
        return new ResponseEntity<>(itemService.addComment(commentDto, itemId, userId), HttpStatus.OK);
    }


}
