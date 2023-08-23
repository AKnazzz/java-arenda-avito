package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;

//	@GetMapping
//	public ResponseEntity<Object> getBookings(@RequestHeader("X-Sharer-User-Id") long userId,
//			@RequestParam(name = "state", defaultValue = "all") String stateParam,
//			@PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
//			@Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
//		BookingState state = BookingState.from(stateParam)
//				.orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
//		log.info("Get booking with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
//		return bookingClient.getBookings(userId, state, from, size);
//	}
//
//	@PostMapping
//	public ResponseEntity<Object> bookItem(@RequestHeader("X-Sharer-User-Id") long userId,
//			@RequestBody @Valid BookItemRequestDto requestDto) {
//		log.info("Creating booking {}, userId={}", requestDto, userId);
//		return bookingClient.bookItem(userId, requestDto);
//	}
//
//	@GetMapping("/{bookingId}")
//	public ResponseEntity<Object> getBooking(@RequestHeader("X-Sharer-User-Id") long userId,
//			@PathVariable Long bookingId) {
//		log.info("Get booking {}, userId={}", bookingId, userId);
//		return bookingClient.getBooking(userId, bookingId);
//	}

    @PostMapping
    public ResponseEntity<Object> bookingCreate(@Valid @RequestBody BookingRequestDto bookingRequestDto,
            @RequestHeader(value = "X-Sharer-User-Id") @Positive Long userId) {
        log.info("Получен POST запрос по эндпоинту /bookings на добавление Bookings {}.", bookingRequestDto);
        return bookingClient.bookingCreate(bookingRequestDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> confirm(@PathVariable("bookingId") @Positive long bookingId,
            @RequestParam(name = "approved") boolean approved,
            @RequestHeader("X-Sharer-User-Id") @Positive long userOwnerId) {
        log.info(
                "Получен PATCH запрос по эндпоинту /bookings/{} от User(Owner) c ID {} статус подтверждения "
                        + "(approved = {}) Booking с ID {}.",
                bookingId, userOwnerId, approved, bookingId);
        return bookingClient.confirm(bookingId, approved, userOwnerId);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getById(@PathVariable("bookingId") @Positive long bookingId,
            @RequestHeader(value = "X-Sharer-User-Id") @Positive Long userId) { // либо автор брони, либо Owner
        log.info("Получен GET запрос по эндпоинту /bookings/{} от User c ID {} на получение Booking с ID {}.",
                bookingId, userId, bookingId);
        return bookingClient.getById(bookingId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllByBooker(
            @RequestParam(defaultValue = "ALL") String state,
            @RequestParam(defaultValue = "0", required = false) @PositiveOrZero int from,
            @RequestParam(defaultValue = "10", required = false) @Positive int size,
            @RequestHeader("X-Sharer-User-Id") @Positive long bookerId) {
        log.info(
                "Получен GET запрос по эндпоинту /bookings от User c ID {} на получение списка всех Booking этого User.",
                bookerId);
        return bookingClient.getAllByBooker(state, from, size, bookerId);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllByOwner(
            @RequestParam(defaultValue = "ALL") String state,
            @RequestParam(defaultValue = "0", required = false) @PositiveOrZero int from,
            @RequestParam(defaultValue = "10", required = false) @Positive int size,
            @RequestHeader("X-Sharer-User-Id") @Positive long ownerId) {
        log.info(
                "Получен GET запрос по эндпоинту /bookings/owner от User c ID {} на получение списка всех Booking всех "
                        + "Items для которых он Owner.",
                ownerId);
        return bookingClient.getAllByOwner(state, from, size, ownerId);
    }

}
