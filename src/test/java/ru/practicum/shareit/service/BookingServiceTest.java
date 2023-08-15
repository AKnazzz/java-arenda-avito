package ru.practicum.shareit.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.enums.StatusType;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.UnsupportedStatusException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;

@Transactional
@DisplayName("Тесты класса BookingService")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceTest {
    private static final User mockUser1 = new User(1L, "Дональд", "donald@yandex.ru");
    private static final User mockUser2 = new User(2L, "Джо", "joe@yandex.ru");
    private static final Item mockItem1 = new Item(1L, "Серп", "Часть чего то важного", true, mockUser1, 1L);
    private static final Booking mockBooking1 = new Booking(1L, LocalDateTime.of(2021, 12, 12, 1, 1), LocalDateTime.of(2021, 12, 22, 1, 1), mockItem1, mockUser2, StatusType.APPROVED);
    private static final Booking mockBooking2 = new Booking(2L, LocalDateTime.of(2024, 12, 12, 1, 1), LocalDateTime.of(2024, 12, 22, 1, 1), mockItem1, mockUser2, StatusType.APPROVED);

    @Mock
    ItemRepository itemRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    BookingRepository bookingRepository;

    @InjectMocks
    BookingServiceImpl bookingServiceImpl;

    private MockitoSession session;

    @BeforeEach
    void init() {
        session = Mockito.mockitoSession().initMocks(this).startMocking();
        bookingServiceImpl = new BookingServiceImpl(bookingRepository, itemRepository, userRepository);
    }

    @AfterEach
    void tearDown() {
        session.finishMocking();
    }

    @Test
    @DisplayName("Тест на создание Booking")
    public void createTest() {
        User user = mockUser2;
        Item item = mockItem1;
        BookingRequestDto bookingRequestDto = BookingMapper.bookingToRequest(mockBooking1);
        LocalDateTime start = LocalDateTime.now().plusDays(3);
        LocalDateTime end = LocalDateTime.now().plusDays(7);
        bookingRequestDto.setStart(start);
        bookingRequestDto.setEnd(end);
        Booking booking = BookingMapper.requestToBooking(bookingRequestDto);
        booking.setItem(item);
        booking.getItem().setAvailable(true);
        Mockito.when(userRepository.findById(Mockito.any())).thenReturn(Optional.of(user));
        Mockito.when(itemRepository.findById(Mockito.any())).thenReturn(Optional.of(item));
        Mockito.when(bookingRepository.save(Mockito.any())).thenReturn(booking);
        BookingResponseDto result = bookingServiceImpl.create(bookingRequestDto, user.getId());
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(booking.getId());
    }

    @Test
    @DisplayName("Тест createBooking_whenItemIsNotAvailable_shouldThrowResponseStatusException")
    void createBooking_whenItemIsNotAvailable_shouldThrowResponseStatusException() {
        LocalDateTime start = LocalDateTime.now().plusDays(3);
        LocalDateTime end = LocalDateTime.now().plusDays(7);
        User user = mockUser1;
        Item item = mockItem1;
        BookingRequestDto bookingRequestDto = new BookingRequestDto(item.getId(), start, end);
        item.setOwner(user);
        item.setAvailable(false);
        Mockito.when(userRepository.findById(Mockito.any())).thenReturn(Optional.of(user));
        Mockito.when(itemRepository.findById(Mockito.any())).thenReturn(Optional.of(item));
        assertThrows(ResponseStatusException.class, () -> {
            bookingServiceImpl.create(bookingRequestDto, user.getId());
        });
    }

    @Test
    @DisplayName("Тест createBooking_whenStartIsAfterEnd_shouldThrowResponseStatusException")
    void createBooking_whenStartIsAfterEnd_shouldThrowResponseStatusException() {
        LocalDateTime start = LocalDateTime.now().plusDays(3);
        LocalDateTime end = LocalDateTime.now().plusDays(1);
        User user = mockUser1;
        Item item = mockItem1;
        item.setOwner(user);
        BookingRequestDto bookingRequestDto = new BookingRequestDto(item.getId(), start, end);
        Mockito.when(userRepository.findById(Mockito.any())).thenReturn(Optional.of(user));
        Mockito.when(itemRepository.findById(Mockito.any())).thenReturn(Optional.of(item));
        assertThrows(ResponseStatusException.class, () -> {
            bookingServiceImpl.create(bookingRequestDto, user.getId());
        });
    }

    @Test
    @DisplayName("Тест createBooking_whenStartIsBeforeNow_shouldThrowResponseStatusException")
    void createBooking_whenStartIsBeforeNow_shouldThrowResponseStatusException() {
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(1);
        BookingRequestDto bookingRequestDto = new BookingRequestDto(1L, start, end);
        User user = mockUser1;
        Item item = mockItem1;
        item.setOwner(user);
        item.setAvailable(true);
        Mockito.when(userRepository.findById(Mockito.any())).thenReturn(Optional.of(user));
        Mockito.when(itemRepository.findById(Mockito.any())).thenReturn(Optional.of(item));
        assertThrows(ResponseStatusException.class, () -> {
            bookingServiceImpl.create(bookingRequestDto, 1L);
        });
    }

    @Test
    @DisplayName("Тест createBooking_whenUserIsOwner_shouldThrowResponseStatusException")
    void createBooking_whenUserIsOwner_shouldThrowResponseStatusException() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(3);
        BookingRequestDto bookingRequestDto = new BookingRequestDto(1L, start, end);
        User user = mockUser1;
        Item item = mockItem1;
        item.setOwner(user);
        item.setAvailable(true);
        Mockito.when(userRepository.findById(Mockito.any())).thenReturn(Optional.of(user));
        Mockito.when(itemRepository.findById(Mockito.any())).thenReturn(Optional.of(item));
        assertThrows(ResponseStatusException.class, () -> {
            bookingServiceImpl.create(bookingRequestDto, 1L);
        });
    }

    @Test
    @DisplayName("Тест createBooking_InvalidStartAndEndTimes_ThrowsBadRequestException")
    public void createBooking_InvalidStartAndEndTimes_ThrowsBadRequestException() {
        BookingRequestDto bookingRequestDto = BookingMapper.bookingToRequest(mockBooking1);
        bookingRequestDto.setStart(LocalDateTime.now().plusHours(2));
        bookingRequestDto.setEnd(LocalDateTime.now().plusHours(1));
        User user = mockUser1;
        Item item = mockItem1;
        item.setAvailable(true);
        item.setOwner(user);
        Mockito.when(userRepository.findById(Mockito.any())).thenReturn(Optional.of(user));
        Mockito.when(itemRepository.findById(Mockito.any())).thenReturn(Optional.of(item));
        assertThrows(ResponseStatusException.class, () -> {
            bookingServiceImpl.create(bookingRequestDto, 1L);
        });
    }

    @Test
    @DisplayName("Тест testConfirmBooking_Success")
    void testConfirmBooking_Success() {
        Booking booking = mockBooking1;
        booking.setBooker(mockUser2);
        booking.setStatus(StatusType.WAITING);
        booking.getItem().setOwner(mockUser1);
        Mockito.when(bookingRepository.findById(Mockito.any())).thenReturn(Optional.of(booking));
        Mockito.when(userRepository.existsById(Mockito.any())).thenReturn(true);
        bookingServiceImpl.confirm(booking.getId(), booking.getItem().getOwner().getId(), true);
        Assertions.assertEquals(StatusType.APPROVED, booking.getStatus());
        Mockito.verify(bookingRepository, times(1)).findById(1L);
        Mockito.verify(userRepository, times(1)).existsById(1L);
    }

    @Test
    @DisplayName("Тест testConfirmBooking_BookingNotFound")
    void testConfirmBooking_BookingNotFound() {
        Mockito.when(bookingRepository.findById(1L)).thenReturn(Optional.empty());
        Exception exception = assertThrows(EntityNotFoundException.class, () -> bookingServiceImpl.confirm(1L, 1L, true));
        Assertions.assertEquals("Нет такого Booking.", exception.getMessage());
    }

    @Test
    @DisplayName("Тест testConfirmBooking_UserOwnerNotFound")
    void testConfirmBooking_UserOwnerNotFound() {
        Booking booking = mockBooking1;
        booking.setStatus(StatusType.WAITING);
        Mockito.when(bookingRepository.findById(Mockito.any())).thenReturn(Optional.of(booking));
        Mockito.when(userRepository.existsById(Mockito.any())).thenReturn(false);
        Exception exception = assertThrows(EntityNotFoundException.class, () -> bookingServiceImpl.confirm(1L, 1L, true));
        Assertions.assertEquals("Нет такого User.", exception.getMessage());
    }

    @Test
    @DisplayName("Тест testConfirmBooking_AlreadyApproved")
    void testConfirmBooking_AlreadyApproved() {
        Booking booking = mockBooking1;
        booking.setStatus(StatusType.APPROVED);
        Mockito.when(bookingRepository.findById(Mockito.any())).thenReturn(Optional.of(booking));
        User userOwner = mockUser1;
        Mockito.when(userRepository.existsById(Mockito.any())).thenReturn(true);
        assertThrows(ResponseStatusException.class, () -> bookingServiceImpl.confirm(1L, 1L, true));
        Exception exception = assertThrows(ResponseStatusException.class, () -> bookingServiceImpl.confirm(1L, 1L, true));
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, ((ResponseStatusException) exception).getStatus());
        Assertions.assertEquals("400 BAD_REQUEST \"StateType для данного Booking уже изменен на APPROVED\"", exception.getMessage());
    }

    @Test
    @DisplayName("Тест testConfirmBooking_InvalidStatus")
    void testConfirmBooking_InvalidStatus() {
        Booking booking = mockBooking1;
        booking.setStatus(StatusType.REJECTED);
        Mockito.when(bookingRepository.findById(Mockito.any())).thenReturn(Optional.of(booking));
        User userOwner = mockUser1;
        Mockito.when(userRepository.existsById(Mockito.any())).thenReturn(true);
        Assertions.assertThrows(ResponseStatusException.class, () -> bookingServiceImpl.confirm(1L, 1L, false));
        Exception exception = assertThrows(ResponseStatusException.class, () -> bookingServiceImpl.confirm(1L, 1L, false));
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, ((ResponseStatusException) exception).getStatus());
        Assertions.assertEquals("400 BAD_REQUEST \"StateType для данного Booking уже изменен на APPROVED\"", exception.getMessage());
    }

    @Test
    @DisplayName("Тест testConfirmBooking_Unauthorized")
    void testConfirmBooking_Unauthorized() {
        Booking booking = mockBooking1;
        booking.setStatus(StatusType.WAITING);
        Mockito.when(bookingRepository.findById(Mockito.any())).thenReturn(Optional.of(booking));
        User userOwner = mockUser2;
        Mockito.when(userRepository.existsById(Mockito.any())).thenReturn(true);
        assertThrows(ResponseStatusException.class, () -> bookingServiceImpl.confirm(1L, 2L, false));
        Exception exception = assertThrows(ResponseStatusException.class, () -> bookingServiceImpl.confirm(1L, 2L, false));
        Assertions.assertEquals(HttpStatus.NOT_FOUND, ((ResponseStatusException) exception).getStatus());
        Assertions.assertEquals("404 NOT_FOUND \"StateType для данного Booking может установить только UserOwner\"", exception.getMessage());
    }

    @Test
    @DisplayName("Тест testRejectBookingStatus")
    public void testRejectBookingStatus() {
        boolean approved = false;
        Booking booking = mockBooking1;
        booking.setBooker(mockUser2);
        booking.setStatus(StatusType.WAITING);
        booking.getItem().setOwner(mockUser1);
        Mockito.when(bookingRepository.findById(Mockito.any())).thenReturn(Optional.of(booking));
        Mockito.when(userRepository.existsById(Mockito.any())).thenReturn(true);
        BookingResponseDto response = bookingServiceImpl.confirm(booking.getId(), booking.getItem().getOwner().getId(), approved);
        Assertions.assertEquals(StatusType.REJECTED, booking.getStatus());
    }

    @Test
    @DisplayName("Тест testInvalidBookingStatus")
    public void testInvalidBookingStatus() {
        boolean approved = true;
        Booking booking = mockBooking1;
        booking.setStatus(StatusType.APPROVED);
        Mockito.when(bookingRepository.findById(Mockito.any())).thenReturn(Optional.of(booking));
        Mockito.when(userRepository.existsById(Mockito.any())).thenReturn(true);
        Exception exception = assertThrows(ResponseStatusException.class, () -> {
            bookingServiceImpl.confirm(1L, 1L, approved);
        });
        Assertions.assertEquals("400 BAD_REQUEST \"StateType для данного Booking уже изменен на APPROVED\"", exception.getMessage());
    }

    @Test
    @DisplayName("Тест testGetByIdExistingBooking")
    public void testGetByIdExistingBooking() {
        Booking booking = mockBooking1;
        Long bookingId = 1L;
        Long userId = 1L;
        BookingResponseDto expectedResponse = BookingMapper.bookingToResponse(booking);
        Mockito.when(bookingRepository.findById(Mockito.any())).thenReturn(Optional.of(booking));
        BookingResponseDto actualResponse = bookingServiceImpl.getById(bookingId, userId);
        Assertions.assertEquals(expectedResponse, actualResponse);
    }

    @Test
    @DisplayName("Тест testGetByIdNonExistingBooking")
    public void testGetByIdNonExistingBooking() {
        Long bookingId = 1L;
        Long userId = 1L;
        Mockito.when(bookingRepository.findById(Mockito.any())).thenReturn(null);
        //  Mockito.when(userRepository.existsById(Mockito.any())).thenReturn(true);
        assertThrows(NullPointerException.class, () -> {
            bookingServiceImpl.getById(bookingId, userId);
        });
    }

    @Test
    @DisplayName("Тест testGetByIdDataAccess")
    public void testGetByIdDataAccess() {
        Booking booking = mockBooking1;
        booking.setBooker(mockUser2);
        booking.getItem().setOwner(mockUser1);
        Long bookingId = 1L;
        Long userId = 3L;
        Mockito.when(bookingRepository.findById(Mockito.any())).thenReturn(Optional.of(booking));
        assertThrows(ResponseStatusException.class, () -> {
            bookingServiceImpl.getById(bookingId, userId);
        }, "Booking not found");
    }

    @Test
    @DisplayName("Тест testGetByIdCorrectData")
    public void testGetByIdCorrectData() {
        Booking booking = mockBooking1;
        Long bookingId = 1L;
        Long userId = 1L;
        BookingResponseDto expectedResponse = BookingMapper.bookingToResponse(booking);
        Mockito.when(bookingRepository.findById(Mockito.any())).thenReturn(Optional.of(booking));
        BookingResponseDto actualResponse = bookingServiceImpl.getById(bookingId, userId);
        Assertions.assertEquals(expectedResponse, actualResponse);
    }

    @Test
    @DisplayName("Тест testGetAllByBookerAllState")
    public void testGetAllByBookerAllState() {
        User user = mockUser1;
        Mockito.when(userRepository.findById(Mockito.any())).thenReturn(Optional.of(user));
        List<Booking> bookingList = Arrays.asList(mockBooking1, mockBooking2);
        Mockito.when(bookingRepository.findBookingByBookerOrderByStartDesc(Mockito.any(), Mockito.any())).thenReturn(bookingList);
        int from = 0;
        int size = 10;
        String state = "ALL";
        Long bookerId = 1L;
        List<BookingResponseDto> expectedResponse = Arrays.asList(BookingMapper.bookingToResponse(mockBooking1), BookingMapper.bookingToResponse(mockBooking2));
        List<BookingResponseDto> actualResponse = bookingServiceImpl.getAllByBooker(from, size, state, bookerId);
        Assertions.assertEquals(expectedResponse, actualResponse);
    }

    @Test
    @DisplayName("Тест testGetAllByBookerWaitingState")
    public void testGetAllByBookerWaitingState() {
        User user = mockUser1;
        Mockito.when(userRepository.findById(Mockito.any())).thenReturn(Optional.of(user));
        Booking booking = mockBooking1;
        booking.setBooker(user);
        booking.setStatus(StatusType.WAITING);
        Booking booking2 = mockBooking2;
        booking2.setBooker(user);
        booking2.setStatus(StatusType.WAITING);
        List<Booking> bookingList = Arrays.asList(booking, booking2);
        Mockito.when(bookingRepository.findBookingByBookerAndStatusOrderByStartDesc(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(bookingList);
        int from = 0;
        int size = 10;
        String state = "WAITING";
        Long bookerId = 1L;
        List<BookingResponseDto> expectedResponse = Arrays.asList(BookingMapper.bookingToResponse(booking), BookingMapper.bookingToResponse(booking2));
        List<BookingResponseDto> actualResponse = bookingServiceImpl.getAllByBooker(from, size, state, bookerId);
        Assertions.assertEquals(expectedResponse, actualResponse);
    }

    @Test
    @DisplayName("Тест testGetAllByBookerREJECTEDState")
    public void testGetAllByBookerREJECTEDState() {
        User user = mockUser1;
        Mockito.when(userRepository.findById(Mockito.any())).thenReturn(Optional.of(user));
        Booking booking = mockBooking1;
        booking.setBooker(user);
        booking.setStatus(StatusType.REJECTED);
        Booking booking2 = mockBooking2;
        booking2.setBooker(user);
        booking2.setStatus(StatusType.REJECTED);
        List<Booking> bookingList = Arrays.asList(booking, booking2);
        Mockito.when(bookingRepository.findBookingByBookerAndStatusOrderByStartDesc(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(bookingList);
        int from = 0;
        int size = 10;
        String state = "REJECTED";
        Long bookerId = 1L;
        List<BookingResponseDto> expectedResponse = Arrays.asList(BookingMapper.bookingToResponse(booking), BookingMapper.bookingToResponse(booking2));
        List<BookingResponseDto> actualResponse = bookingServiceImpl.getAllByBooker(from, size, state, bookerId);
        Assertions.assertEquals(expectedResponse, actualResponse);
    }

    @Test
    @DisplayName("Тест testGetAllByBookerCurrentState")
    public void testGetAllByBookerCurrentState() {
        User user = mockUser1;
        Mockito.when(userRepository.findById(Mockito.any())).thenReturn(Optional.of(user));
        Booking booking = mockBooking1;
        booking.setBooker(user);
        Booking booking2 = mockBooking2;
        booking2.setBooker(user);
        List<Booking> bookingList = Arrays.asList(booking, booking2);
        Mockito.when(bookingRepository.findBookingByBookerAndStartBeforeAndEndAfterOrderByStartDesc(Mockito.any(),
                Mockito.any(LocalDateTime.class), Mockito.any(LocalDateTime.class), Mockito.any())).thenReturn(bookingList);
        int from = 0;
        int size = 10;
        String state = "CURRENT";
        Long bookerId = 1L;
        List<BookingResponseDto> expectedResponse = Arrays.asList(BookingMapper.bookingToResponse(booking), BookingMapper.bookingToResponse(booking2));
        List<BookingResponseDto> actualResponse = bookingServiceImpl.getAllByBooker(from, size, state, bookerId);
        Assertions.assertEquals(expectedResponse, actualResponse);
    }

    @Test
    @DisplayName("Тест testGetAllByBookerPastState")
    public void testGetAllByBookerPastState() {
        User user = mockUser1;
        Mockito.when(userRepository.findById(Mockito.any())).thenReturn(Optional.of(user));
        Booking booking = mockBooking1;
        booking.setBooker(user);
        Booking booking2 = mockBooking2;
        booking2.setBooker(user);
        List<Booking> bookingList = Arrays.asList(booking, booking2);
        Mockito.when(bookingRepository.findBookingByBookerAndEndBeforeOrderByStartDesc(Mockito.any(),
                Mockito.any(LocalDateTime.class), Mockito.any())).thenReturn(bookingList);
        int from = 0;
        int size = 10;
        String state = "PAST";
        Long bookerId = 1L;
        List<BookingResponseDto> expectedResponse = Arrays.asList(BookingMapper.bookingToResponse(booking), BookingMapper.bookingToResponse(booking2));
        List<BookingResponseDto> actualResponse = bookingServiceImpl.getAllByBooker(from, size, state, bookerId);
        Assertions.assertEquals(expectedResponse, actualResponse);
    }

    @Test
    @DisplayName("Тест testGetAllByBookerFutureState")
    public void testGetAllByBookerFutureState() {
        User user = mockUser1;
        Mockito.when(userRepository.findById(Mockito.any())).thenReturn(Optional.of(user));
        Booking booking = mockBooking1;
        booking.setBooker(user);
        Booking booking2 = mockBooking2;
        booking2.setBooker(user);
        List<Booking> bookingList = Arrays.asList(booking, booking2);
        Mockito.when(bookingRepository.findBookingByBookerAndStartAfterOrderByStartDesc(Mockito.any(),
                Mockito.any(LocalDateTime.class), Mockito.any())).thenReturn(bookingList);
        int from = 0;
        int size = 10;
        String state = "FUTURE";
        Long bookerId = 1L;
        List<BookingResponseDto> expectedResponse = Arrays.asList(BookingMapper.bookingToResponse(booking), BookingMapper.bookingToResponse(booking2));
        List<BookingResponseDto> actualResponse = bookingServiceImpl.getAllByBooker(from, size, state, bookerId);

        Assertions.assertEquals(expectedResponse, actualResponse);
    }

    @Test
    @DisplayName("Тест testGetAllByBookerUnsupportedState")
    public void testGetAllByBookerUnsupportedState() {
        User user = mockUser1;
        Mockito.when(userRepository.findById(Mockito.any())).thenReturn(Optional.of(user));
        int from = 0;
        int size = 10;
        String state = "INVALID_STATE";
        Long bookerId = 1L;
        assertThrows(UnsupportedStatusException.class, () -> {
            bookingServiceImpl.getAllByBooker(from, size, state, bookerId);
        });
    }

    @Test
    @DisplayName("Тест маппинга testDtoToBooking")
    public void testDtoToBooking() {
        BookingDto bookingDto = BookingMapper.bookingToDto(mockBooking1);
        Booking booking = BookingMapper.dtoToBooking(bookingDto);

        Assertions.assertEquals(bookingDto.getId(), booking.getId());
        Assertions.assertEquals(bookingDto.getStart(), booking.getStart());
        Assertions.assertEquals(bookingDto.getEnd(), booking.getEnd());
        Assertions.assertEquals(bookingDto.getItem(), booking.getItem());
        Assertions.assertEquals(bookingDto.getBooker(), booking.getBooker());
        Assertions.assertEquals(bookingDto.getStatus(), booking.getStatus());
    }

    @Test
    @DisplayName("Тест testGetAllByOwnerAllState")
    public void testGetAllByOwnerAllState() {
        User user = mockUser1;
        Mockito.when(userRepository.findById(Mockito.any())).thenReturn(Optional.of(user));
        List<Booking> bookingList = Arrays.asList(mockBooking1, mockBooking2);
        Mockito.when(bookingRepository.getAllBookingsForOwner(Mockito.any(), Mockito.any()))
                .thenReturn(bookingList);
        int from = 0;
        int size = 10;
        String state = "ALL";
        Long ownerId = 1L;
        List<BookingResponseDto> expectedResponse = Arrays.asList(BookingMapper.bookingToResponse(mockBooking1), BookingMapper.bookingToResponse(mockBooking2));
        List<BookingResponseDto> actualResponse = bookingServiceImpl.getAllByOwner(from, size, state, ownerId);

        Assertions.assertEquals(expectedResponse, actualResponse);
    }

    @Test
    @DisplayName("Тест testGetAllByOwnerWaitingState")
    public void testGetAllByOwnerWaitingState() {
        User user = mockUser1;
        Mockito.when(userRepository.findById(Mockito.any())).thenReturn(Optional.of(user));
        List<Booking> bookingList = Arrays.asList(mockBooking1, mockBooking2);
        Mockito.when(bookingRepository.getBookingsForOwnerByStatus(Mockito.any(), Mockito.any(),
                Mockito.any())).thenReturn(bookingList);

        int from = 0;
        int size = 10;
        String state = "WAITING";
        Long ownerId = 1L;
        List<BookingResponseDto> expectedResponse = Arrays.asList(BookingMapper.bookingToResponse(mockBooking1), BookingMapper.bookingToResponse(mockBooking2));
        List<BookingResponseDto> actualResponse = bookingServiceImpl.getAllByOwner(from, size, state, ownerId);

        Assertions.assertEquals(expectedResponse, actualResponse);
    }

    @Test
    @DisplayName("Тест testGetAllByOwnerRejectedState")
    public void testGetAllByOwnerRejectedState() {
        User user = mockUser1;
        Mockito.when(userRepository.findById(Mockito.any())).thenReturn(Optional.of(user));
        List<Booking> bookingList = Arrays.asList(mockBooking1, mockBooking2);
        Mockito.when(bookingRepository.getBookingsForOwnerByStatus(Mockito.any(), Mockito.any(),
                Mockito.any())).thenReturn(bookingList);
        int from = 0;
        int size = 10;
        String state = "REJECTED";
        Long ownerId = 1L;
        List<BookingResponseDto> expectedResponse = Arrays.asList(BookingMapper.bookingToResponse(mockBooking1), BookingMapper.bookingToResponse(mockBooking2));
        List<BookingResponseDto> actualResponse = bookingServiceImpl.getAllByOwner(from, size, state, ownerId);

        Assertions.assertEquals(expectedResponse, actualResponse);
    }

    @Test
    @DisplayName("Тест testGetAllByOwnerCurrentState")
    public void testGetAllByOwnerCurrentState() {
        User user = mockUser1;
        Mockito.when(userRepository.findById(Mockito.any())).thenReturn(Optional.of(user));
        List<Booking> bookingList = Arrays.asList(mockBooking1, mockBooking2);
        Mockito.when(bookingRepository.getCurrentBookingForOwner(Mockito.any(), Mockito.any(LocalDateTime.class), Mockito.any(LocalDateTime.class),
                Mockito.any())).thenReturn(bookingList);
        int from = 0;
        int size = 10;
        String state = "CURRENT";
        Long ownerId = 1L;
        List<BookingResponseDto> expectedResponse = Arrays.asList(BookingMapper.bookingToResponse(mockBooking1), BookingMapper.bookingToResponse(mockBooking2));
        List<BookingResponseDto> actualResponse = bookingServiceImpl.getAllByOwner(from, size, state, ownerId);

        Assertions.assertEquals(expectedResponse, actualResponse);
    }

    @Test
    @DisplayName("Тест testGetAllByOwnerPastState")
    public void testGetAllByOwnerPastState() {
        User user = mockUser1;
        Mockito.when(userRepository.findById(Mockito.any())).thenReturn(Optional.of(user));
        List<Booking> bookingList = Arrays.asList(mockBooking1, mockBooking2);
        Mockito.when(bookingRepository.getPastBookingForOwner(Mockito.any(), Mockito.any(LocalDateTime.class),
                Mockito.any())).thenReturn(bookingList);

        int from = 0;
        int size = 10;
        String state = "PAST";
        Long ownerId = 1L;
        List<BookingResponseDto> expectedResponse = Arrays.asList(BookingMapper.bookingToResponse(mockBooking1), BookingMapper.bookingToResponse(mockBooking2));
        List<BookingResponseDto> actualResponse = bookingServiceImpl.getAllByOwner(from, size, state, ownerId);

        Assertions.assertEquals(expectedResponse, actualResponse);
    }

    @Test
    @DisplayName("Тест testGetAllByOwnerFutureCase")
    public void testGetAllByOwnerFutureCase() {
        User user = mockUser1;
        Mockito.when(userRepository.findById(Mockito.any())).thenReturn(Optional.of(user));
        Booking booking = mockBooking1;
        booking.setBooker(user);
        Booking booking2 = mockBooking2;
        booking2.setBooker(user);
        List<Booking> bookingList = Arrays.asList(booking, booking2);
        Mockito.when(bookingRepository.getFutureBookingForOwner(Mockito.any(),
                Mockito.any(LocalDateTime.class), Mockito.any())).thenReturn(bookingList);
        int from = 0;
        int size = 10;
        String state = "FUTURE";
        Long bookerId = 1L;
        List<BookingResponseDto> expectedResponse = Arrays.asList(BookingMapper.bookingToResponse(booking), BookingMapper.bookingToResponse(booking2));
        List<BookingResponseDto> actualResponse = bookingServiceImpl.getAllByOwner(from, size, state, bookerId);

        Assertions.assertEquals(expectedResponse, actualResponse);
    }

    @Test
    @DisplayName("Тест testGetAllByOwnerDefaultCase")
    public void testGetAllByOwnerDefaultCase() {
        User user = mockUser1;
        Mockito.when(userRepository.findById(Mockito.any())).thenReturn(Optional.of(user));
        int from = 0;
        int size = 10;
        String state = "INVALID_STATE";
        Long bookerId = 1L;
        assertThrows(UnsupportedStatusException.class, () -> {
            bookingServiceImpl.getAllByOwner(from, size, state, bookerId);
        });
    }
}

