package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@RestController
@RequestMapping(path = "/users")
@Slf4j
@RequiredArgsConstructor
@Validated
public class UserController {

    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> userCreate(@Valid @RequestBody UserDto userDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(userDto);
        }
        log.info("Получен POST запрос по эндпоинту /users на добавление User {}.", userDto);
        return userClient.userCreate(userDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getById(@PathVariable @Positive Long id) {
        log.info("Получен GET запрос по эндпоинту /users/{} на получение User с ID {}.", id, id);
        return userClient.getById(id);
    }

    @GetMapping
    public ResponseEntity<Object> geAllUsers() {
        log.info("Получен GET запрос по эндпоинту /users на получение всех существующих Users.");
        return userClient.geAllUsers();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteUser(@PathVariable @Positive Long id) {
        log.info("Получен DELETE запрос по эндпоинту /users/{} на удаление User с ID {}.", id, id);
        return userClient.deleteUser(id);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateUser(@PathVariable @Positive Long id, @RequestBody UserDto userDto) {
        log.info("Получен PATCH запрос по эндпоинту /users/{} на одновление данных User с ID {}.", id, id);
        return userClient.updateUser(id, userDto);
    }
}
