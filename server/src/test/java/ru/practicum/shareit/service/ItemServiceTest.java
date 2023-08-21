package ru.practicum.shareit.service;

import lombok.RequiredArgsConstructor;
import org.junit.Assert;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.enums.StatusType;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@Transactional
@DisplayName("Тесты класса ItemService")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceTest {

    private User mockUser1;
    private User mockUser2;
    private Item mockItem1;
    private Item mockItem2;
    private Booking mockBooking1;
    private Booking mockBooking2;
    @Mock
    ItemRepository itemRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    BookingRepository bookingRepository;
    @Mock
    CommentRepository commentRepository;

    @InjectMocks
    ItemServiceImpl itemService;

    private MockitoSession session;

    @BeforeEach
    void init() {
        session = Mockito.mockitoSession().initMocks(this).startMocking();
        itemService = new ItemServiceImpl(itemRepository, userRepository, bookingRepository, commentRepository);
        mockUser1 = new User(1L, "Дональд", "donald@yandex.ru");
        mockUser2 = new User(2L, "Джо", "joe@yandex.ru");
        mockItem1 = new Item(1L, "Серп", "Часть чего то важного", true, mockUser1, 1L);
        mockItem2 = new Item(2L, "Молот", "Сила заключённая в предмете", true, mockUser2, 2L);
        mockBooking1 = new Booking(1L, LocalDateTime.of(2021, 12, 12, 1, 1), LocalDateTime.of(2021, 12, 22, 1, 1), mockItem1, mockUser2, StatusType.APPROVED);
        mockBooking2 = new Booking(2L, LocalDateTime.of(2024, 12, 12, 1, 1), LocalDateTime.of(2024, 12, 22, 1, 1), mockItem1, mockUser2, StatusType.APPROVED);
    }

    @AfterEach
    void tearDown() {
        session.finishMocking();
    }

    @Test
    @DisplayName("Тест на создание Item")
    public void createTest() {
        ItemDto itemDto = ItemMapper.itemToDto(mockItem1);
        Mockito
                .when(userRepository.findById(1L))
                .thenReturn(Optional.of(mockUser1));
        Mockito
                .when(itemRepository.save(Mockito.any()))
                .thenReturn(ItemMapper.dtoToItem(itemDto));
        ItemDto itemDto2 = itemService.create(itemDto, mockUser1.getId());
        Mockito.verify(itemRepository, Mockito.times(1)).save(mockItem1);
        Assertions.assertNotNull(itemDto2);
        Assertions.assertEquals(itemDto.getName(), itemDto2.getName());
    }


    @Test
    @DisplayName("Тест на создание Item повторные тесты")
    public void testCreateItem_UserNotFound() {
        ItemDto itemDto = ItemMapper.itemToDto(mockItem1);

        Mockito
                .when(userRepository.findById(mockUser1.getId()))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(EntityNotFoundException.class, () -> {
            itemService.create(itemDto, mockUser1.getId());
        });
        Mockito.verify(userRepository, Mockito.times(1)).findById(mockUser1.getId());
        Mockito.verify(itemRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    @DisplayName("Тест на обновление Item")
    public void updateItemTest() {
        ItemDto itemDto1 = ItemMapper.itemToDto(mockItem1);
        ItemDto itemDto2 = ItemMapper.itemToDto(mockItem2);
        User owner = mockUser1;

        Mockito.when(userRepository.save(Mockito.any()))
                .thenReturn(owner);

        Mockito.when((userRepository.findById(Mockito.any())))
                .thenReturn(Optional.of(mockUser1));

        Mockito.when(itemRepository.save(Mockito.any()))
                .thenReturn(ItemMapper.dtoToItem(itemDto1));

        Mockito.when((itemRepository.findById(Mockito.any())))
                .thenReturn(Optional.ofNullable(ItemMapper.dtoToItem(itemDto1)));

        userRepository.save(owner);
        itemService.create(itemDto1, owner.getId());
        itemDto2.setId(1L);

        Item item2 = ItemMapper.dtoToItem(itemService.update(1L, itemDto2, owner.getId()));

        Mockito.verify(itemRepository, Mockito.times(1)).save(ItemMapper.dtoToItem(itemDto1));
        Assertions.assertEquals(mockItem2.getName(), item2.getName());
        Assertions.assertEquals(mockItem2.getDescription(), item2.getDescription());
        Assertions.assertEquals(mockItem2.getAvailable(), item2.getAvailable());
    }

    @Test
    @DisplayName("Тест на обновление Item не User Owner")
    public void testUpdateItemWithWrongOwner() {
        Long itemId = 1L;
        Long userOwnerId = 2L;
        ItemDto itemDto = ItemMapper.itemToDto(mockItem1);
        User owner = new User();
        owner.setId(1L);
        Item item = new Item();
        item.setId(itemId);
        item.setName("Old Name");
        item.setDescription("Old Description");
        item.setAvailable(false);
        item.setOwner(owner);
        Mockito.when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        Exception exception = Assertions.assertThrows(ResponseStatusException.class, () -> {
            itemService.update(itemId, itemDto, userOwnerId);
        });
        Assertions.assertEquals(HttpStatus.NOT_FOUND, ((ResponseStatusException) exception).getStatus());
    }

    @Test
    @DisplayName("Тест на получение Item по ID для User Owner")
    public void getByIdTest() {
        ItemDto itemDto = ItemMapper.itemToDto(mockItem1);
        User owner = mockUser1;
        Mockito
                .when(userRepository.save(Mockito.any()))
                .thenReturn(owner);
        Mockito
                .when((userRepository.findById(Mockito.any())))
                .thenReturn(Optional.of(mockUser1));
        Mockito
                .when(itemRepository.save(Mockito.any()))
                .thenReturn(ItemMapper.dtoToItem(itemDto));
        Mockito
                .when((itemRepository.findById(Mockito.any())))
                .thenReturn(Optional.ofNullable(ItemMapper.dtoToItem(itemDto)));
        userRepository.save(owner);
        Item item = ItemMapper.dtoToItem(itemService.create(itemDto, owner.getId()));

        Assert.assertEquals("Серп", itemService.getById(1L, 1L).getName());
        Assert.assertEquals(Long.valueOf(1), itemService.getById(1L, 1L).getId());
        Assert.assertEquals("Часть чего то важного", itemService.getById(1L, 1L).getDescription());
        Assert.assertEquals(true, itemService.getById(1L, 1L).getAvailable());
        Assert.assertEquals(Long.valueOf(1), itemService.getById(1L, 1L).getRequestId());
    }

    @Test
    @DisplayName("Тест на получение Item по ID для User Не Owner")
    public void testGetByIdForUser() {
        Item item = mockItem1;
        User owner = mockUser1;
        User commentator = mockUser2;

        Comment comment1 = Comment.builder()
                .id(1L)
                .text("Comment 1")
                .item(item)
                .author(commentator)
                .build();
        Comment comment2 = Comment.builder()
                .id(2L)
                .text("Comment 2")
                .item(item)
                .author(commentator)
                .build();

        List<Comment> comments = List.of(comment1, comment2);

        Mockito
                .when(userRepository.save(owner))
                .thenReturn(owner);
        Mockito
                .when(userRepository.save(commentator))
                .thenReturn(commentator);
        Mockito
                .when((userRepository.findById(1L)))
                .thenReturn(Optional.of(mockUser1));
        Mockito
                .when(itemRepository.save(Mockito.any()))
                .thenReturn(item);
        Mockito
                .when((itemRepository.findById(Mockito.any())))
                .thenReturn(Optional.ofNullable(item));

        userRepository.save(owner);
        userRepository.save(commentator);
        itemService.create(ItemMapper.itemToDto(item), owner.getId());
        ItemResponseDto itemResponseDto = ItemResponseDto.create(null, null, item, comments);
        itemResponseDto.setComments(CommentMapper.listCommentsToListResponse(comments));

        ItemResponseDto itemResponseDto2 = itemService.getById(item.getId(), commentator.getId());

        Assertions.assertEquals(item.getId(), itemResponseDto2.getId());
        Assertions.assertEquals(item.getName(), itemResponseDto2.getName());
        Assertions.assertEquals(item.getDescription(), itemResponseDto2.getDescription());
        Assertions.assertEquals(item.getAvailable(), itemResponseDto2.getAvailable());
        Assertions.assertNull(itemResponseDto2.getLastBooking());
        Assertions.assertNull(itemResponseDto2.getNextBooking());
        Assertions.assertEquals(CommentMapper.listCommentsToListResponse(comments), itemResponseDto.getComments());
    }

    @Test
    @DisplayName("Тест на получение списка всех своих Item для User Owner")
    public void testGetAllByOwner() {
        User userOwner = mockUser1;
        User commentator = mockUser2;
        Mockito
                .when(userRepository.findById(userOwner.getId()))
                .thenReturn(Optional.of(userOwner));

        Item item1 = mockItem1;
        Item item2 = mockItem2;
        item2.setOwner(userOwner);

        Mockito
                .when(itemRepository.save(mockItem1))
                .thenReturn(item1);
        Mockito
                .when(itemRepository.save(mockItem2))
                .thenReturn(item2);

        List<Item> items = Arrays.asList(item1, item2);
        Mockito
                .when(itemRepository.findAllByOwnerIdOrderByIdAsc(1L, PageRequest.of(0, 10)))
                .thenReturn(items);

        Comment comment1 = Comment.builder()
                .id(1L)
                .text("Comment 1")
                .item(item1)
                .author(commentator)
                .build();
        Comment comment2 = Comment.builder()
                .id(2L)
                .text("Comment 2")
                .item(item1)
                .author(commentator)
                .build();

        List<Comment> comments = List.of(comment1, comment2);

        Mockito
                .when(commentRepository.findAllByItem_Id(item1.getId()))
                .thenReturn(comments);

        Booking lastBooking = mockBooking1;
        Booking nextBooking = mockBooking2;

        Mockito
                .when(bookingRepository.findFirstByItemIdAndStartBeforeAndStatusOrderByEndDesc(
                        eq(1L), Mockito.any(LocalDateTime.class), eq(StatusType.APPROVED)))
                .thenReturn(lastBooking);
        Mockito
                .when(bookingRepository.findFirstByItemIdAndStartAfterAndStatusOrderByStartAsc(
                        eq(1L), Mockito.any(LocalDateTime.class), eq(StatusType.APPROVED)))
                .thenReturn(nextBooking);

        itemService.create(ItemMapper.itemToDto(item1), userOwner.getId());
        itemService.create(ItemMapper.itemToDto(item2), userOwner.getId());
        bookingRepository.save(lastBooking);
        bookingRepository.save(nextBooking);

        List<ItemResponseDto> itemResponseDtos = itemService.getAllByOwner(0, 10, userOwner.getId());

        Assertions.assertEquals(2, itemResponseDtos.size());
        ItemResponseDto itemResponseDto1 = itemResponseDtos.get(0);
        Assertions.assertEquals(item1.getId(), itemResponseDto1.getId());
        Assertions.assertEquals(comments.size(), itemResponseDto1.getComments().size());
        Assertions.assertEquals(lastBooking.getId(), itemResponseDto1.getLastBooking().getId());
        Assertions.assertEquals(nextBooking.getId(), itemResponseDto1.getNextBooking().getId());

        ItemResponseDto itemResponseDto2 = itemResponseDtos.get(1);
        Assertions.assertEquals(item2.getId(), itemResponseDto2.getId());
        Assertions.assertTrue(itemResponseDto2.getComments().isEmpty());
        Assertions.assertNull(itemResponseDto2.getLastBooking());
        Assertions.assertNull(itemResponseDto2.getNextBooking());
    }

    @Test
    @DisplayName("Тест на удаление Item по ID от User Owner")
    public void deleteByIdTest() {
        ItemDto itemDto = ItemMapper.itemToDto(mockItem1);

        Mockito
                .when(userRepository.existsById(Mockito.any()))
                .thenReturn(true);
        Mockito
                .when(itemRepository.findById(1L))
                .thenReturn(Optional.of(mockItem1));
        Mockito
                .when(itemRepository.existsById(Mockito.any()))
                .thenReturn(true);
        ItemResponseDto deletedItem = itemService.getById(1L, 1L);
        Assertions.assertEquals(mockUser1.getId(), 1L);

        itemService.deleteById(1L, 1L);

        Mockito.verify(itemRepository, Mockito.times(1)).deleteItemByIdAndOwner_Id(1L, 1L);
        Mockito.verify(itemRepository, Mockito.times(2)).findById(Mockito.any());
        Mockito.verify(itemRepository, Mockito.never()).save(Mockito.any());
        Mockito.verify(itemRepository, Mockito.times(1)).existsById(Mockito.any());
        Mockito.verify(userRepository, Mockito.never()).findById(1L);
        Mockito.verify(userRepository, Mockito.times(1)).existsById(1L);
        Mockito.verifyNoMoreInteractions(userRepository);
    }

    @Test
    @DisplayName("Тест на удаление Item по ID от некорректного User")
    public void testDeleteById_UserNotFound() {
        Item item = mockItem1;
        User user = mockUser2;

        Mockito
                .when(itemRepository.findById(item.getId()))
                .thenReturn(Optional.of(new Item()));
        Mockito
                .when(userRepository.existsById(user.getId()))
                .thenReturn(false);

        Assertions.assertThrows(EntityNotFoundException.class, () -> {
            itemService.deleteById(item.getId(), user.getId());
        });
    }

    @Test
    @DisplayName("Тест на удаление Item по ID от User НЕ Owner")
    public void testDeleteById_UserNotOwner() {
        Long id = 1L;
        Long userOwnerId = 2L;

        User owner = new User();
        owner.setId(3L);

        Item item = new Item();
        item.setOwner(owner);

        Mockito.when(itemRepository.findById(id)).thenReturn(Optional.of(item));
        Mockito.when(userRepository.existsById(userOwnerId)).thenReturn(true);

        Assertions.assertThrows(ResponseStatusException.class, () -> {
            itemService.deleteById(id, userOwnerId);
        });
    }

    @Test
    @DisplayName("Тест на поиск Item по text")
    public void testSearch() {
        int from = 0;
        int size = 10;
        String text = "Очень нужная вещь!";

        User user = mockUser1;

        List<Item> items = List.of(mockItem1, mockItem2);

        Pageable pageable = PageRequest.of(from, size);
        Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        Mockito.when(itemRepository.findByText(text, pageable)).thenReturn(items);

        List<ItemDto> result = itemService.search(from, size, text, user.getId());

        Assertions.assertEquals(2, result.size());
    }

    @Test
    @DisplayName("Тест на маппинг ListItems в ListDto")
    public void testListItemsToListDto() {

        Item item1 = mockItem1;
        Item item2 = mockItem2;
        List<Item> items = new ArrayList<>();
        items.add(item1);
        items.add(item2);

        List<ItemDto> result = ItemMapper.listItemsToListDto(items);

        assertThat(result.size(), is(2));
        assertThat(result.get(0).getId(), is(item1.getId()));
        assertThat(result.get(0).getName(), is(item1.getName()));
        assertThat(result.get(0).getDescription(), is(item1.getDescription()));
        assertThat(result.get(1).getId(), is(item2.getId()));
        assertThat(result.get(1).getName(), is(item2.getName()));
        assertThat(result.get(1).getDescription(), is(item2.getDescription()));
    }

    @Test
    @DisplayName("Тест на маппинг ListItems в ListResponseDto")
    public void testListItemsToListResponseDto() {
        Item item1 = mockItem1;
        Item item2 = mockItem2;
        List<Item> items = new ArrayList<>();
        items.add(item1);
        items.add(item2);

        List<ItemResponseDto> result = ItemMapper.listItemsToListResponseDto(items);

        assertThat(result.size(), is(2));
        assertThat(result.get(0).getId(), is(item1.getId()));
        assertThat(result.get(0).getName(), is(item1.getName()));
        assertThat(result.get(0).getDescription(), is(item1.getDescription()));
        assertThat(result.get(1).getId(), is(item2.getId()));
        assertThat(result.get(1).getName(), is(item2.getName()));
        assertThat(result.get(1).getDescription(), is(item2.getDescription()));
    }

    @Test
    @DisplayName("Тест на поиск Item с пустым текстовым запросом")
    public void testSearchEmptyText() {
        int from = 0;
        int size = 10;
        String text = null;
        Long userId = 1L;

        User user = new User();
        user.setId(userId);

        List<Item> items = new ArrayList<>();
        items.add(new Item());
        items.add(new Item());

        Pageable pageable = PageRequest.of(from, size);
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        List<ItemDto> result = itemService.search(from, size, text, userId);

        Assertions.assertEquals(0, result.size());
    }

    @Test
    @DisplayName("Тест на добавление Comment к Item")
    public void addCommentTest() {
        User user = mockUser1;
        Item item = mockItem1;
        Booking booking = mockBooking1;

        CommentDto commentDto = CommentDto.builder()
                .text("comment")
                .build();

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        Mockito.when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        Mockito.when(bookingRepository.findAllByBooker_IdAndItem_IdAndEndBefore(Mockito.any(), Mockito.any(), Mockito.any(LocalDateTime.class)))
                .thenReturn(Collections.singletonList(booking));
        Mockito.when(commentRepository.save(any(Comment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CommentResponseDto result = itemService.addComment(commentDto, 1L, 1L);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(commentDto.getText(), result.getText());
        Assertions.assertEquals(user.getId(), 1L);
        Assertions.assertEquals(item.getId(), 1L);

        Mockito.verify(userRepository).findById(1L);
        Mockito.verify(itemRepository).findById(1L);
        Mockito.verify(bookingRepository).findAllByBooker_IdAndItem_IdAndEndBefore(Mockito.any(), Mockito.any(), Mockito.any(LocalDateTime.class));
        Mockito.verify(commentRepository).save(any(Comment.class));
    }
}




