package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingResponseDto> bookingCreate(@Valid @RequestBody BookingRequestDto bookingRequestDto,
            @RequestHeader(value = "X-Sharer-User-Id") @Positive Long userId) {
        log.info("Получен POST запрос по эндпоинту /bookings на добавление Bookings {}.", bookingRequestDto);
        return new ResponseEntity<>(bookingService.create(bookingRequestDto, userId), HttpStatus.CREATED);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<BookingResponseDto> confirm(@PathVariable("bookingId") @Positive long bookingId,
            @RequestParam(name = "approved") boolean approved,
            @RequestHeader("X-Sharer-User-Id") @Positive long userOwnerId) {
        log.info(
                "Получен PATCH запрос по эндпоинту /bookings/{} от User(Owner) c ID {} статус подтверждения "
                        + "(approved = {}) Booking с ID {}.",
                bookingId, userOwnerId, approved, bookingId);
        return new ResponseEntity<>(bookingService.confirm(bookingId, userOwnerId, approved), HttpStatus.OK);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingResponseDto> getById(@PathVariable("bookingId") @Positive long bookingId,
            @RequestHeader(value = "X-Sharer-User-Id") @Positive Long userId) { // либо автор брони, либо Owner
        log.info("Получен GET запрос по эндпоинту /bookings/{} от User c ID {} на получение Booking с ID {}.",
                bookingId, userId, bookingId);
        return new ResponseEntity<>(bookingService.getById(bookingId, userId), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<BookingResponseDto>> getAllByBooker(
            @RequestParam(defaultValue = "ALL") String state,
            @RequestParam(defaultValue = "0", required = false) @PositiveOrZero int from,
            @RequestParam(defaultValue = "10", required = false) @Positive int size,
            @RequestHeader("X-Sharer-User-Id") @Positive long bookerId) {
        log.info(
                "Получен GET запрос по эндпоинту /bookings от User c ID {} на получение списка всех Booking этого User.",
                bookerId);
        return new ResponseEntity<>(bookingService.getAllByBooker(from, size, state, bookerId), HttpStatus.OK);
    }

    @GetMapping("/owner")
    public ResponseEntity<List<BookingResponseDto>> getAllByOwner(
            @RequestParam(defaultValue = "ALL") String state,
            @RequestParam(defaultValue = "0", required = false) @PositiveOrZero int from,
            @RequestParam(defaultValue = "10", required = false) @Positive int size,
            @RequestHeader("X-Sharer-User-Id") @Positive long ownerId) {
        log.info(
                "Получен GET запрос по эндпоинту /bookings/owner от User c ID {} на получение списка всех Booking всех "
                        + "Items для которых он Owner.",
                ownerId);
        return new ResponseEntity<>(bookingService.getAllByOwner(from, size, state, ownerId), HttpStatus.OK);
    }
}
