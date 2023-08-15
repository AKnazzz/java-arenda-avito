package ru.practicum.shareit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.enums.StatusType;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = ItemController.class)
@AutoConfigureMockMvc
@DisplayName("Тесты класса ItemController")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemControllerTest {
    final ObjectMapper objectMapper;
    final MockMvc mockMvc;
    @MockBean
    ItemService itemService;

    private static final User mockUser1 = new User(1L, "Дональд", "donald@yandex.ru");
    private static final User mockUser2 = new User(2L, "Джо", "joe@yandex.ru");
    private static final Item mockItem1 = new Item(1L, "Серп", "Часть чего то важного", true, mockUser1, 1L);
    private static final Item mockItem2 = new Item(2L, "Молот", "Сила заключённая в предмете", true, mockUser2, 2L);
    private static final Booking mockBooking1 = new Booking(1L, LocalDateTime.of(2021, 12, 12, 1, 1), LocalDateTime.of(2021, 12, 22, 1, 1), mockItem1, mockUser2, StatusType.APPROVED);
    private static final Booking mockBooking2 = new Booking(2L, LocalDateTime.of(2024, 12, 12, 1, 1), LocalDateTime.of(2024, 12, 22, 1, 1), mockItem1, mockUser2, StatusType.APPROVED);

    @Test
    @DisplayName("Тест на эндпоинт @PostMapping создания Item")
    @SneakyThrows
    void itemCreateTest() {
        Item item = mockItem1;
        ItemDto itemDto = ItemMapper.itemToDto(mockItem1);
        ItemResponseDto itemResponseDto = ItemResponseDto.create(mockBooking1, mockBooking2, item, List.of());

        Mockito
                .when(itemService.create(Mockito.any(), Mockito.any()))
                .thenReturn(itemDto);

        Mockito
                .when((itemService.getById(Mockito.any(), Mockito.any())))
                .thenReturn(itemResponseDto);

        itemService.create(itemDto, 1L);

        mockMvc.perform(MockMvcRequestBuilders.post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andDo(print());

        Mockito.verify(itemService).create(itemDto, 1L);
    }

    @Test
    @DisplayName("Тест на эндпоинт @GetMapping на получение Item по ID")
    @SneakyThrows
    void getByIdTest() {
        Item item = mockItem1;
        ItemDto itemDto = ItemMapper.itemToDto(mockItem1);
        ItemResponseDto itemResponseDto = ItemResponseDto.create(mockBooking1, mockBooking2, item, List.of());

        Mockito
                .when(itemService.create(Mockito.any(), Mockito.any()))
                .thenReturn(itemDto);

        Mockito
                .when((itemService.getById(Mockito.any(), Mockito.any())))
                .thenReturn(itemResponseDto);

        itemService.create(itemDto, 1L);

        mockMvc.perform(MockMvcRequestBuilders.get("/items/{itemId}", itemDto.getId())
                        .header("X-Sharer-User-Id", 1L))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(itemService).getById(itemDto.getId(), 1L);
    }


    @Test
    @DisplayName("Тест на эндпоинт @PatchMapping на одновление Item по ID")
    @SneakyThrows
    void itemUpdateTest() {
        Item item = mockItem1;
        ItemDto itemDto = ItemMapper.itemToDto(mockItem1);
        ItemResponseDto itemResponseDto = ItemResponseDto.create(mockBooking1, mockBooking2, item, List.of());

        Mockito
                .when(itemService.create(Mockito.any(), Mockito.any()))
                .thenReturn(itemDto);

        Mockito
                .when((itemService.getById(Mockito.any(), Mockito.any())))
                .thenReturn(itemResponseDto);

        Mockito
                .when((itemService.update(Mockito.anyLong(), Mockito.any(ItemDto.class), Mockito.anyLong())))
                .thenReturn(itemDto);

        itemService.create(itemDto, 1L);

        mockMvc.perform(MockMvcRequestBuilders.patch("/items/{itemId}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("Тест на эндпоинт @GetMapping на получение всех Item для User Owner")
    @SneakyThrows
    void geAllItemsByOwnerTest() {
        User owner = mockUser1;
        Item item1 = mockItem1;
        Item item2 = mockItem2;
        item2.setOwner(owner);
        int from = 3;
        int size = 2;
        mockMvc.perform(MockMvcRequestBuilders.get("/items?from={from}&size={size}", from, size)
                        .header("X-Sharer-User-Id", owner.getId()))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(itemService).getAllByOwner(3, 2, owner.getId());
    }

    @Test
    @DisplayName("Тест на эндпоинт @DeleteMapping на удаленчие Item по ID")
    @SneakyThrows
    void deleteItemTest() {
        Item item = mockItem1;
        ItemDto itemDto = ItemMapper.itemToDto(mockItem1);
        ItemResponseDto itemResponseDto = ItemResponseDto.create(mockBooking1, mockBooking2, item, List.of());

        Mockito
                .when(itemService.create(Mockito.any(), Mockito.any()))
                .thenReturn(itemDto);

        Mockito
                .when((itemService.getById(Mockito.any(), Mockito.any())))
                .thenReturn(itemResponseDto);

        itemService.create(itemDto, 1L);

        mockMvc.perform(MockMvcRequestBuilders.delete("/items/{itemId}", 1L)
                        .header("X-Sharer-User-Id", 1L))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(itemService).deleteById(1L, 1L);
    }

    @Test
    @DisplayName("Тест на эндпоинт @GetMapping на поиск всех Item по текстовому запросу")
    @SneakyThrows
    void searchItemTest() {
        User user = mockUser1;
        String text = "Серп";
        mockMvc.perform(MockMvcRequestBuilders.get("/items/search")
                        .param("from", "0")
                        .param("size", "10")
                        .param("text", text)
                        .header("X-Sharer-User-Id", user.getId()))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(itemService).search(0, 10, text, 1L);
    }

    @Test
    @DisplayName("Тест на эндпоинт @PostMapping создания Comment")
    @SneakyThrows
    void addCommentTest() {
        User user = mockUser1;
        Item item = mockItem1;

        Comment comment = Comment.builder()
                .id(1L)
                .text("Comment")
                .item(item)
                .author(user)
                .build();
        CommentDto commentDto = CommentMapper.commentToDto(comment);
        CommentResponseDto commentResponseDto = CommentMapper.toResponseDto(comment);

        Mockito
                .when(itemService.addComment(ArgumentMatchers.any(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong()))
                .thenReturn(commentResponseDto);
        mockMvc.perform(MockMvcRequestBuilders.post("/items/{itemId}/comment", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentResponseDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }

    @Test
    @DisplayName("Тест на ItemMapper с исключением Dto.")
    public void dtoToItemNullDtoTest() {
        ItemDto itemDto = null;

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            ItemMapper.dtoToItem(itemDto);
        });
    }

    @Test
    @DisplayName("Тест на ItemMapper с исключением Dto.")
    public void itemToDtoNullDtoTest() {
        Item item = null;

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            ItemMapper.itemToDto(item);
        });
    }

    @Test
    @DisplayName("Тест на ItemMapper с исключением Dto.")
    public void toResposeItemNullDtoTest() {
        Item item = null;

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            ItemMapper.toResposeItem(item);
        });
    }

}



