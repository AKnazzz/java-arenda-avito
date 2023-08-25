package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

import java.util.List;

public interface BookingService {

    BookingResponseDto create(BookingRequestDto bookingResponseDto, Long userId);

    BookingResponseDto confirm(Long bookingId, Long userOwnerId, boolean approved); // Подтверждение
    // или отклонение запроса на бронирование. Может быть выполнено только владельцем вещи.
    // Затем статус бронирования становится либо APPROVED, либо REJECTED.

    BookingResponseDto getById(Long bookingId, Long userId); // Получение данных о конкретном бронировании
    // (включая его статус). Может быть выполнено либо автором бронирования, либо владельцем вещи,
    // к которой относится бронирование.

    List<BookingResponseDto> getAllByBooker(int from, int size, String state, Long bookerId);

    List<BookingResponseDto> getAllByOwner(int from, int size, String state, Long bookerId);


}
