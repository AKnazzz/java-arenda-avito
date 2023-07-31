package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.enums.StatusType;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.UnsupportedStatusException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@Slf4j
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public BookingResponseDto create(BookingRequestDto bookingRequestDto, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User не найден."));
        Item item = itemRepository.findById(bookingRequestDto.getItemId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item не найден."));
        if (!item.getAvailable()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Item не доступен для бронирования.");
        }
        if (bookingRequestDto.getStart().isAfter(bookingRequestDto.getEnd()) || bookingRequestDto.getStart()
                .equals(bookingRequestDto.getEnd())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Start time позде End time у Booking");
        }
        if (bookingRequestDto.getStart().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Дата начала не может быть в прошлом");
        }
        if (userId.equals(item.getOwner().getId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "StateType для данного Booking может установить только UserOwner");
        }
        Booking booking = BookingMapper.requestToBooking(bookingRequestDto);

        booking.setStatus(StatusType.WAITING);
        booking.setBooker(user);
        booking.setItem(item);
        bookingRepository.save(booking);
        log.info("Создан Booking {} от User c ID {}.", booking, userId);
        return BookingMapper.bookingToResponse(booking);
    }

    @Override
    @Transactional
    public BookingResponseDto confirm(Long bookingId, Long userOwnerId, boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Booking не найден."));

        if (!userRepository.existsById(userOwnerId)) {
            throw new EntityNotFoundException("User не найден.");
        }

        if (booking.getStatus().equals(StatusType.APPROVED) ||
                booking.getStatus().equals(StatusType.REJECTED)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "StateType для данного Booking уже изменен на APPROVED");
        }
        if (!booking.getStatus().equals(StatusType.WAITING)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "StateType можно поменять только для Booking, в статусе WAITING");
        }
        if (!booking.getItem().getOwner().getId().equals(userOwnerId) || booking.getItem().getOwner().getId()
                .equals(booking.getBooker().getId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "StateType для данного Booking может установить только UserOwner");
        }

        if (approved) {
            booking.setStatus(StatusType.APPROVED);
            log.info("UserOwner c ID {} подтвердил (APPROVED) запрос на Booking с id = {} ", userOwnerId,
                    booking.getId());
        } else {
            booking.setStatus(StatusType.REJECTED);
            log.info("UserOwner c ID {} отклонил (REJECTED) запрос на Booking с id = {} ", userOwnerId,
                    booking.getId());
        }

        return BookingMapper.bookingToResponse(booking);
    }

    @Override
    public BookingResponseDto getById(Long bookingId, Long userId) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Booking не найден."));

        if (!userId.equals(booking.getBooker().getId()) && !userId.equals(booking.getItem().getOwner().getId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Данные о конкретном Booking (включая его статус) может видеть только User Owner или Booker.");
        }
        log.info("Возвращен запрос на бронирование с id = {} ", bookingId);
        return BookingMapper.bookingToResponse(booking);
    }

    @Override
    public List<BookingResponseDto> getAllByBooker(int from, int size, String state, Long bookerId) {
        User user = userRepository.findById(bookerId).orElseThrow(() -> new EntityNotFoundException("User не найден."));
        List<Booking> bookList;
        switch (state) {
            case "ALL": //все
                bookList = bookingRepository.findBookingByBookerOrderByStartDesc(user);
                break;
            case "WAITING": // ожидающие подтверждения
            case "REJECTED": // отклонённые
                bookList = bookingRepository.findBookingByBookerAndStatusOrderByStartDesc(user,
                        StatusType.valueOf(state));
                break;
            case "CURRENT": // текущие
                LocalDateTime dateTime = LocalDateTime.now();
                bookList = bookingRepository.findBookingByBookerAndStartBeforeAndEndAfterOrderByStartDesc(user,
                        dateTime, dateTime);
                break;
            case "PAST": // завершённые
                LocalDateTime dateTime1 = LocalDateTime.now();
                bookList = bookingRepository.findBookingByBookerAndEndBeforeOrderByStartDesc(user, dateTime1);
                break;
            case "FUTURE": // будущие
                LocalDateTime dateTime2 = LocalDateTime.now();
                bookList = bookingRepository.findBookingByBookerAndStartAfterOrderByStartDesc(user, dateTime2);
                break;
            default:
                throw new UnsupportedStatusException("Неподдерживаемый параметр BookingState");
        }
        return bookList.stream().map(BookingMapper::bookingToResponse).collect(Collectors.toList());
    }

    @Override
    public List<BookingResponseDto> getAllByOwner(int from, int size, String state, Long ownerId) {
        userRepository.findById(ownerId).orElseThrow(() -> new EntityNotFoundException("User не найден."));
        List<Booking> bookList;
        switch (state) {
            case "ALL":
                bookList = bookingRepository.getAllBookingsForOwner(ownerId);
                break;
            case "WAITING":
            case "REJECTED":
                bookList = bookingRepository.getBookingsForOwnerByStatus(ownerId, StatusType.valueOf(state));
                break;
            case "CURRENT":
                LocalDateTime dateTime = LocalDateTime.now();
                bookList = bookingRepository.getCurrentBookingForOwner(ownerId, dateTime, dateTime);
                break;
            case "PAST":
                LocalDateTime dateTime1 = LocalDateTime.now();
                bookList = bookingRepository.getPastBookingForOwner(ownerId, dateTime1);
                break;
            case "FUTURE":
                LocalDateTime dateTime2 = LocalDateTime.now();
                bookList = bookingRepository.getFutureBookingForOwner(ownerId, dateTime2);
                break;
            default:
                throw new UnsupportedStatusException("Неподдерживаемый параметр BookingState");
        }
        return bookList.stream().map(BookingMapper::bookingToResponse).collect(Collectors.toList());
    }

}
