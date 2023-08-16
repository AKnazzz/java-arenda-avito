package ru.practicum.shareit.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.enums.StatusType;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;

@Transactional
@DisplayName("Тесты класса ItemRequestService")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestServiceTest {
    private User mockUser1;
    private User mockUser2;
    private Item mockItem1;
    private Item mockItem2;
    private ItemRequest mockItemRequest1;
    private ItemRequest mockItemRequest2;
    @Mock
    ItemRequestRepository itemRequestRepository;
    @Mock
    ItemRepository itemRepository;
    @Mock
    UserRepository userRepository;
    @InjectMocks
    ItemRequestServiceImpl itemRequestServiceImpl;

    private MockitoSession session;

    @BeforeEach
    void init() {
        session = Mockito.mockitoSession().initMocks(this).startMocking();
        itemRequestServiceImpl = new ItemRequestServiceImpl(itemRequestRepository, itemRepository, userRepository);
        mockUser1 = new User(1L, "Дональд", "donald@yandex.ru");
        mockUser2 = new User(2L, "Джо", "joe@yandex.ru");
        mockItem1 = new Item(1L, "Серп", "Часть чего то важного", true, mockUser1, 1L);
        mockItem2 = new Item(2L, "Молот", "Сила заключённая в предмете", true, mockUser2, 2L);
        mockItemRequest1 = new ItemRequest(1L, "Требуется серп", mockUser2, LocalDateTime.of(2021, 12, 12, 1, 1, 1));
        mockItemRequest2 = new ItemRequest(2L, "Требуется молот", mockUser1, LocalDateTime.of(2021, 12, 12, 1, 1, 1));
    }

    @AfterEach
    void tearDown() {
        session.finishMocking();
    }

    @Test
    @DisplayName("Тест на создание itemRequest")
    public void createTest() {
        ItemRequest itemRequest = mockItemRequest1;
        ItemRequestDto itemRequestDto = ItemRequestMapper.itemRequestToDto(mockItemRequest1);
        Mockito
                .when(itemRequestRepository.save(any()))
                .thenReturn(mockItemRequest1);
        Mockito
                .when(userRepository.findById(any()))
                .thenReturn(Optional.of(mockUser1));
        ItemRequestDto itemRequestDto2 = itemRequestServiceImpl.create(itemRequestDto, mockUser1.getId());
        Mockito.verify(itemRepository, never()).save(mockItem1);
        Assertions.assertNotNull(itemRequestDto2);
        Assertions.assertEquals(itemRequestDto.getId(), itemRequestDto2.getId());
        Assertions.assertEquals(itemRequestDto.getDescription(), itemRequestDto2.getDescription());
    }

    @Test
    @DisplayName("Тест на создание itemRequest некорректным User")
    public void create_shouldThrowEntityNotFoundException_whenUserNotFound() {
        ItemRequestDto itemRequestDto = ItemRequestMapper.itemRequestToDto(mockItemRequest1);
        Long userId = 1L;
        Mockito
                .when(userRepository.findById(userId))
                .thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> itemRequestServiceImpl.create(itemRequestDto, userId));
        Mockito.verify(itemRequestRepository, never()).save(any());
    }

    @Test
    @DisplayName("Тест на получение всех itemRequest для конкретного User requestor")
    public void testGetAllForRequestor() {
        User user = mockUser1;
        ItemRequest itemRequest = mockItemRequest1;
        itemRequest.setRequestor(user);
        List<ItemRequest> itemRequests = List.of(itemRequest);
        Mockito
                .when(userRepository.findById(Mockito.any()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(itemRequestRepository.findAllByRequestor_idOrderByCreatedAsc(Mockito.any()))
                .thenReturn(itemRequests);
        Mockito
                .when(itemRepository.findAllByRequestIdOrderByIdAsc(Mockito.any()))
                .thenReturn(List.of());
        List<ItemRequestResponseDto> result = itemRequestServiceImpl.getAllForRequestor(user.getId());
        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Тест на получение всех itemRequest конкретным User requestor у которого нет запросов")
    public void testGetAllForRequestorEmptyList() {
        User user = mockUser1;
        Mockito
                .when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(itemRequestRepository.findAllByRequestor_idOrderByCreatedAsc(user.getId()))
                .thenReturn(List.of());
        List<ItemRequestResponseDto> result = itemRequestServiceImpl.getAllForRequestor(user.getId());
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Тест на получение всех itemRequest")
    public void testGetAllRequests() {
        User user = mockUser1;
        ItemRequest itemRequest1 = mockItemRequest1;
        itemRequest1.setRequestor(user);
        ItemRequest itemRequest2 = mockItemRequest2;
        List<ItemRequest> itemRequests = List.of(itemRequest1, itemRequest2);
        Item item1 = mockItem1;
        Item item2 = mockItem2;
        List<Item> items = List.of(item1, item2);
        Mockito
                .when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(itemRequestRepository.findAllByRequestor_IdNotIn(List.of(user.getId()),
                        PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "created"))))
                .thenReturn(new PageImpl<>(itemRequests));
        Mockito
                .when(itemRepository.findAllByRequestIdOrderByIdAsc(1L))
                .thenReturn(items);
        Mockito
                .when(itemRepository.findAllByRequestIdOrderByIdAsc(2L))
                .thenReturn(new ArrayList<>());

        List<ItemRequestResponseDto> result = itemRequestServiceImpl.getAllRequests(0, 10, user.getId());

        Assertions.assertNotNull(result);
        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals(1L, result.get(0).getId());
        Assertions.assertEquals(2L, result.get(1).getId());
    }

    @Test
    @DisplayName("Тест на получение itemRequest по Id")
    public void testGetById() {
        User user = mockUser1;
        ItemRequest itemRequest = mockItemRequest1;
        itemRequest.setRequestor(user);
        List<Item> items = List.of(mockItem1, mockItem2);
        Mockito
                .when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(itemRequestRepository.findById(itemRequest.getId()))
                .thenReturn(Optional.of(itemRequest));
        Mockito
                .when(itemRepository.findAllByRequestIdOrderByIdAsc(itemRequest.getId()))
                .thenReturn(items);
        ItemRequestResponseDto result = itemRequestServiceImpl.getById(itemRequest.getId(), user.getId());
        Assertions.assertNotNull(result);
        Assertions.assertEquals(itemRequest.getId(), result.getId());
        Assertions.assertEquals(2, result.getItems().size());
    }

    @Test
    @DisplayName("Тест на получение itemRequest по Id некорректным User")
    public void testUserNotFoundException() {
        Long userId = 1L;
        Long requestId = 1L;
        Mockito
                .when(userRepository.findById(userId))
                .thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class,
                () -> itemRequestServiceImpl.getById(requestId, userId));
    }

    @Test
    @DisplayName("Тест на получение несуществующего itemRequest по Id")
    public void testItemRequestNotFoundException() {
        Long requestId = 1L;
        User user = mockUser1;
        Mockito
                .when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(itemRequestRepository.findById(requestId))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> itemRequestServiceImpl.getById(requestId, user.getId()));
    }

    @Test
    @DisplayName("Тест маппинга с использованием метода listItemRequestToDto")
    public void testListItemRequestToDto() {
        ItemRequest itemRequest1 = mockItemRequest1;
        ItemRequest itemRequest2 = mockItemRequest2;
        List<ItemRequest> itemRequests = List.of(itemRequest1, itemRequest2);

        List<ItemRequestDto> result = ItemRequestMapper.listItemRequestToDto(itemRequests);

        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals(1L, result.get(0).getId());
        Assertions.assertEquals(itemRequest1.getDescription(), result.get(0).getDescription());
        Assertions.assertEquals(2L, result.get(1).getId());
        Assertions.assertEquals(itemRequest2.getDescription(), result.get(1).getDescription());
    }

    @Test
    @DisplayName("Тест маппинга с использованием метода ItemRequestToDto c входящим null")
    public void testItemRequestToDtoWithNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            ItemRequestMapper.itemRequestToDto(null);
        });
    }

    @Test
    @DisplayName("Тест маппинга с использованием метода DtoToItemRequest c входящим null")
    void testDtoToItemRequestWithNullDto() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            ItemRequestMapper.dtoToItemRequest(null);
        });

        Assertions.assertEquals("ItemRequestDto can not be null.", exception.getMessage());
    }

    @Test
    @DisplayName("Тест маппинга с использованием метода toItemRequestResponse c входящим null")
    void testToItemRequestResponseWithNullItemRequest() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            ItemRequestMapper.toItemRequestResponse(null);
        });
        Assertions.assertEquals("ItemRequest can not be null.", exception.getMessage());
    }
}
