package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;


@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> itemCreate(@Valid @RequestBody ItemDto itemDto,
            @RequestHeader(value = "X-Sharer-User-Id") Long userOwnerId) {
        log.info("Получен POST запрос по эндпоинту /items от User(Owner) c ID {} на добавление Item {}.", userOwnerId,
                itemDto);
        return itemClient.itemCreate(itemDto, userOwnerId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getById(@PathVariable @Positive Long id,
            @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        log.info("Получен GET запрос по эндпоинту /items/{} от User c ID {} на получение Item с ID {}.", id, userId,
                id);
        return itemClient.getById(id, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllItemsByOwner(
            @RequestParam(defaultValue = "0", required = false) @PositiveOrZero int from,
            @RequestParam(defaultValue = "10", required = false) @Positive int size,
            @RequestHeader(value = "X-Sharer-User-Id") Long userOwnerId) {
        log.info("Получен GET запрос по эндпоинту /items от User(Owner) c ID {} на получение списка всех своих Items.",
                userOwnerId);
        return itemClient.getAllItemsByOwner(from, size, userOwnerId);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteItem(@PathVariable @Positive Long id,
            @RequestHeader(value = "X-Sharer-User-Id") Long userOwnerId) {
        log.info("Получен DELETE запрос по эндпоинту /items/{} от User(Owner) c ID {} на удаление Item с ID {}.", id,
                userOwnerId, id);
        return itemClient.deleteItem(id, userOwnerId);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateItem(@PathVariable @Positive Long id, @RequestBody ItemDto itemDto,
            @RequestHeader(value = "X-Sharer-User-Id") Long userOwnerId) {
        log.info(
                "Получен PATCH запрос по эндпоинту /items/{} от User(Owner) c ID {} на обновление данных Item с ID {}.",
                id, userOwnerId, id);
        return itemClient.updateItem(id, itemDto, userOwnerId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItem(
            @RequestParam(defaultValue = "0", required = false) @PositiveOrZero int from,
            @RequestParam(defaultValue = "10", required = false) @Positive int size,
            @RequestParam(name = "searchText") String searchText,
            @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        log.info(
                "Получен GET запрос по эндпоинту /items/search от User c ID {} на получение списка Item по запросу '{}'.",
                userId, searchText);
        return itemClient.searchItem(from, size, searchText, userId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@PathVariable("itemId") @Positive long itemId,
            @RequestHeader(value = "X-Sharer-User-Id") @Positive Long userId,
            @Valid @RequestBody CommentDto commentDto) {
        log.info("Получен POST запрос по эндпоинту /items/{}/comment от User c ID {} на создание Comment {}.", itemId,
                userId, commentDto);
        return itemClient.addComment(itemId, userId, commentDto);
    }

}
