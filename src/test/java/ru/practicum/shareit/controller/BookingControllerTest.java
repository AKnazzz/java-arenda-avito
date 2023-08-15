package ru.practicum.shareit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.enums.StatusType;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = BookingController.class)
@AutoConfigureMockMvc
@DisplayName("Тесты класса BookingController")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingControllerTest {
    final ObjectMapper objectMapper;
    final MockMvc mockMvc;
    @MockBean
    BookingService bookingService;

    private static final User mockUser1 = new User(1L, "Дональд", "donald@yandex.ru");
    private static final User mockUser2 = new User(2L, "Джо", "joe@yandex.ru");
    private static final Item mockItem1 = new Item(1L, "Серп", "Часть чего то важного", true, mockUser1, 1L);
    private static final Booking mockBooking1 = new Booking(1L, LocalDateTime.of(2021, 12, 12, 1, 1), LocalDateTime.of(2021, 12, 22, 1, 1), mockItem1, mockUser2, StatusType.APPROVED);

    @Test
    @DisplayName("Тест на эндпоинт @PostMapping создания Booking")
    @SneakyThrows
    void bookingCreateTest() {
        Booking booking = mockBooking1;
        BookingDto bookingDto = BookingMapper.bookingToDto(booking);
        BookingRequestDto bookingRequestDto = BookingRequestDto.builder()
                .itemId(booking.getId())
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusMinutes(30))
                .build();
        BookingResponseDto bookingResponseDto = BookingMapper.bookingToResponse(booking);

        Mockito
                .when(bookingService.create(Mockito.any(), Mockito.anyLong()))
                .thenReturn(bookingResponseDto);

        bookingService.create(bookingRequestDto, 1L);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andDo(print());
        Mockito.verify(bookingService).create(bookingRequestDto, 1L);
    }

    @Test
    @DisplayName("Тест на эндпоинт @PatchMapping подтврждение Booking от Owner")
    @SneakyThrows
    void confirmTest() {
        User user = mockUser1;
        Booking booking = mockBooking1;
        boolean approved = true;
        mockMvc.perform(patch("/bookings/{bookingId}?approved={approved}", booking.getId(), approved)
                        .header("X-Sharer-User-Id", user.getId()))
                .andDo(print())
                .andExpect(status().isOk());

        verify(bookingService).confirm(booking.getId(), user.getId(), approved);
    }

    @Test
    @DisplayName("Тест на эндпоинт @GetMapping получения Booking по ID")
    @SneakyThrows
    void getByIdTest() {
        User user = mockUser1;
        Booking booking = mockBooking1;

        mockMvc.perform(get("/bookings/{bookingId}", booking.getId())
                        .header("X-Sharer-User-Id", user.getId()))
                .andExpect(status().isOk())
                .andDo(print());

        verify(bookingService).getById(booking.getId(), user.getId());
    }

    @Test
    @DisplayName("Тест на эндпоинт @GetMapping получение всех своих Booking от User booker")
    @SneakyThrows
    void getAllByBookerTest() {
        User user = mockUser1;
        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", user.getId()))
                .andExpect(status().isOk())
                .andDo(print());

        verify(bookingService).getAllByBooker(0, 10, "ALL", user.getId());
    }

    @Test
    @DisplayName("Тест на эндпоинт @GetMapping получение всех своих Booking от User owner")
    @SneakyThrows
    void getAllByOwnerTest() {
        User user = mockUser1;
        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", user.getId()))
                .andExpect(status().isOk())
                .andDo(print());

        verify(bookingService).getAllByOwner(0, 10, "ALL", user.getId());
    }

}
