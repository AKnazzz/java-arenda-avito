package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ResponseEntity<ItemDto> itemCreate(@RequestBody ItemDto itemDto,
            @RequestHeader(value = "X-Sharer-User-Id") Long userOwnerId) {
        log.info("Получен POST запрос по эндпоинту /items от User(Owner) c ID {} на добавление Item {}.", userOwnerId,
                itemDto);
        return new ResponseEntity<>(itemService.create(itemDto, userOwnerId), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemResponseDto> getById(@PathVariable Long id,
            @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        log.info("Получен GET запрос по эндпоинту /items/{} от User c ID {} на получение Item с ID {}.", id, userId,
                id);
        return new ResponseEntity<>(itemService.getById(id, userId), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<ItemResponseDto>> geAllItemsByOwner(
            @RequestParam(defaultValue = "0", required = false) int from,
            @RequestParam(defaultValue = "10", required = false) int size,
            @RequestHeader(value = "X-Sharer-User-Id") Long userOwnerId) {
        log.info("Получен GET запрос по эндпоинту /items от User(Owner) c ID {} на получение списка всех своих Items.",
                userOwnerId);
        return new ResponseEntity<>(itemService.getAllByOwner(from, size, userOwnerId), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteItem(@PathVariable Long id,
            @RequestHeader(value = "X-Sharer-User-Id") Long userOwnerId) {
        log.info("Получен DELETE запрос по эндпоинту /items/{} от User(Owner) c ID {} на удаление Item с ID {}.", id,
                userOwnerId, id);
        itemService.deleteById(id, userOwnerId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateItem(@PathVariable Long id, @RequestBody ItemDto itemDto,
            @RequestHeader(value = "X-Sharer-User-Id") Long userOwnerId) {
        log.info(
                "Получен PATCH запрос по эндпоинту /items/{} от User(Owner) c ID {} на обновление данных Item с ID {}.",
                id, userOwnerId, id);
        return new ResponseEntity<>(itemService.update(id, itemDto, userOwnerId), HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDto>> searchItem(
            @RequestParam(defaultValue = "0", required = false) int from,
            @RequestParam(defaultValue = "10", required = false) int size,
            @RequestParam(name = "text") String searchText,
            @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        log.info(
                "Получен GET запрос по эндпоинту /items/search от User c ID {} на получение списка Item по запросу '{}'.",
                userId, searchText);
        return new ResponseEntity<>(itemService.search(from, size, searchText, userId), HttpStatus.OK);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<CommentResponseDto> addComment(@PathVariable("itemId") long itemId,
            @RequestHeader(value = "X-Sharer-User-Id") Long userId,
            @RequestBody CommentDto commentDto) {
        log.info("Получен POST запрос по эндпоинту /items/{}/comment от User c ID {} на создание Comment {}.", itemId,
                userId, commentDto);
        return new ResponseEntity<>(itemService.addComment(commentDto, itemId, userId), HttpStatus.OK);
    }


}
