package ru.practicum.shareit.user.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.util.ValidationGroup;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequestMapping(path = "/users")
@Slf4j
@Validated
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @Validated(ValidationGroup.Create.class)
    public ResponseEntity<UserDto> userCreate(@Valid @RequestBody UserDto userDto) {
        log.info("Получен POST запрос по эндпоинту /users на добавление User {}.", userDto);
        return new ResponseEntity<>(userService.create(userDto), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getById(@PathVariable @Positive Long id) {
        log.info("Получен GET запрос по эндпоинту /users/{} на получение User с ID {}.", id, id);
        return new ResponseEntity<>(userService.getById(id), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> geAllUsers() {
        log.info("Получен GET запрос по эндпоинту /users на получение всех существующих Users.");
        return new ResponseEntity<>(userService.getAll(), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable @Positive Long id) {
        log.info("Получен DELETE запрос по эндпоинту /users/{} на удаление User с ID {}.", id, id);
        userService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable @Positive Long id, @RequestBody UserDto userDto) {
        log.info("Получен PATCH запрос по эндпоинту /users/{} на одновление данных User с ID {}.", id, id);
        return new ResponseEntity<>(userService.update(id, userDto), HttpStatus.OK);
    }

}
