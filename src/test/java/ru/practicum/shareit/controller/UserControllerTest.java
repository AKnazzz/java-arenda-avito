package ru.practicum.shareit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc
@DisplayName("Тесты класса UserController")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserControllerTest {
    final ObjectMapper objectMapper;
    final MockMvc mockMvc;
    @MockBean
    UserService userService;

    private User mockUser1;
    private User mockUser2;

    @BeforeEach
    void setUp() {
        mockUser1 = new User(1L, "Дональд", "donald@yandex.ru");
        mockUser2 = new User(2L, "Джо", "joe@yandex.ru");
    }

    @Test
    @DisplayName("Тест на эндпоинт @PostMapping создания User")
    @SneakyThrows
    void userCreateTest() {
        User user = mockUser1;
        UserDto userDto = UserMapper.userToDto(user);

        Mockito
                .when(userService.create(Mockito.any()))
                .thenReturn(userDto);

        mockMvc.perform(
                        post("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(userDto))
                                .characterEncoding(StandardCharsets.UTF_8)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andDo(print());

        Mockito.verify(userService).create(userDto);
    }

    @Test
    @DisplayName("Тест на эндпоинт @GetMapping на получение User по ID")
    @SneakyThrows
    void getByIdTest() {
        User user = mockUser1;
        UserDto userDto = UserMapper.userToDto(user);

        Mockito
                .when(userService.create(Mockito.any()))
                .thenReturn(userDto);

        Mockito
                .when((userService.getById(Mockito.any())))
                .thenReturn(userDto);

        userService.create(userDto);

        mockMvc.perform(get("/users/{id}", user.getId()))
                .andDo(print())
                .andExpect(status().isOk());

        Mockito.verify(userService).getById(userDto.getId());
    }

    @Test
    @DisplayName("Тест на эндпоинт @GetMapping на получение всех User")
    @SneakyThrows
    void geAllUsersTest() {
        User user1 = mockUser1;
        UserDto userDto1 = UserMapper.userToDto(user1);
        User user2 = mockUser2;
        UserDto userDto2 = UserMapper.userToDto(user2);

        Mockito
                .when(userService.create(userDto1))
                .thenReturn(userDto1);
        Mockito
                .when((userService.getById(1L)))
                .thenReturn(userDto1);
        Mockito
                .when(userService.create(userDto2))
                .thenReturn(userDto2);
        Mockito
                .when((userService.getById(2L)))
                .thenReturn(userDto2);

        userService.create(userDto1);
        userService.create(userDto2);

        mockMvc.perform(get("/users"))
                .andDo(print())
                .andExpect(status().isOk());

        Mockito.verify(userService).getAll();
    }

    @Test
    @DisplayName("Тест на эндпоинт @DeleteMapping на удаление User по ID")
    @SneakyThrows
    void deleteUserTest() {
        User user = mockUser1;
        UserDto userDto = UserMapper.userToDto(user);

        Mockito
                .when(userService.create(Mockito.any()))
                .thenReturn(userDto);

        Mockito
                .when((userService.getById(Mockito.any())))
                .thenReturn(userDto);

        userService.create(userDto);

        mockMvc.perform(delete("/users/{userId}", userDto.getId()))
                .andDo(print())
                .andExpect(status().isOk());

        Mockito.verify(userService).deleteById(1L);

    }

    @Test
    @DisplayName("Тест на эндпоинт @PatchMapping на одновление User по ID")
    @SneakyThrows
    void updateUserTest() {
        User user1 = mockUser1;
        UserDto userDto1 = UserMapper.userToDto(user1);
        User user2 = mockUser2;
        user2.setId(1L);
        UserDto userDto2 = UserMapper.userToDto(user2);

        Mockito
                .when(userService.create(userDto1))
                .thenReturn(userDto1);
        Mockito
                .when((userService.getById(1L)))
                .thenReturn(userDto1);
        Mockito
                .when(userService.create(userDto2))
                .thenReturn(userDto2);
        Mockito
                .when((userService.getById(2L)))
                .thenReturn(userDto2);

        Mockito
                .when(userService.update(1L, userDto2))
                .thenReturn(userDto2);

        mockMvc.perform(
                        patch("/users/{userId}", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(userDto2))
                                .characterEncoding(StandardCharsets.UTF_8)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }
}
