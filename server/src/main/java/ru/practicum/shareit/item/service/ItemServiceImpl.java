package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
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
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@Slf4j
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    private static final String USER_NOT_FOUND_ERROR = "Нет такого User.";
    private static final String ITEM_NOT_FOUND_ERROR = "Нет такого Item.";

    @Override
    @Transactional
    public ItemDto create(ItemDto itemDto, Long userOwnerId) {
        Item item = ItemMapper.dtoToItem(itemDto);
        item.setOwner(
                userRepository.findById(userOwnerId).orElseThrow(() -> {
                    log.info(USER_NOT_FOUND_ERROR);
                    return new EntityNotFoundException(USER_NOT_FOUND_ERROR);
                }));
        item = itemRepository.save(item);
        log.info("Создана вещь c id = {} ", item.getId());
        return ItemMapper.itemToDto(item);
    }

    @Override
    @Transactional
    public ItemDto update(Long id, ItemDto itemDto, Long userOwnerId) {
        Item item = itemRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(ITEM_NOT_FOUND_ERROR));
        if (!item.getOwner().getId().equals(userOwnerId)) {
            log.info("Пользователь User с ID {} не является Owner.", userOwnerId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        if (itemDto.getName() != null && !itemDto.getName().isBlank()) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null && !itemDto.getDescription().isBlank()) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        itemRepository.save(item);
        log.info("Item с ID {} обновлён.", id);
        return ItemMapper.itemToDto(item);
    }

    @Override
    public ItemResponseDto getById(Long id, Long userId) {
        Item item = itemRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(ITEM_NOT_FOUND_ERROR));
        List<Comment> comments = commentRepository.findAllByItem_Id(id);
        Booking lastBooking = bookingRepository.findFirstByItemIdAndStartBeforeAndStatusOrderByEndDesc(item.getId(),
                LocalDateTime.now(), StatusType.APPROVED);
        Booking nextBooking = bookingRepository.findFirstByItemIdAndStartAfterAndStatusOrderByStartAsc(item.getId(),
                LocalDateTime.now(), StatusType.APPROVED);

        if (item.getOwner().getId().equals(userId)) {  // случай для User Owner - с данными бронивани и комментариями
            ItemResponseDto itemResponseDto = ItemResponseDto.create(lastBooking, nextBooking, item, comments);
            log.info("Item с ID {} получена для User Owner с ID {}.", id, userId);
            return itemResponseDto;
        }

        ItemResponseDto itemResponseDto = ItemMapper.toResposeItem(item); // случай для User - только с комментариями
        itemResponseDto.setComments(CommentMapper.listCommentsToListResponse(comments));
        log.info("Item с ID {} получена для User с ID {}.", id, userId);
        return itemResponseDto;
    }

    @Override
    public List<ItemResponseDto> getAllByOwner(int from, int size, Long userOwnerId) {
        userRepository.findById(userOwnerId).orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND_ERROR));
        Pageable pageable = PageRequest.of(from == 0 ? 0 : (from / size), size);

        List<ItemResponseDto> itemResponseDtos = itemRepository.findAllByOwnerIdOrderByIdAsc(userOwnerId, pageable)
                .stream()
                .map(item -> {
                    List<Comment> comments = commentRepository.findAllByItem_Id(item.getId());
                    Booking lastBooking = bookingRepository.findFirstByItemIdAndStartBeforeAndStatusOrderByEndDesc(
                            item.getId(), LocalDateTime.now(), StatusType.APPROVED);
                    Booking nextBooking = bookingRepository.findFirstByItemIdAndStartAfterAndStatusOrderByStartAsc(
                            item.getId(), LocalDateTime.now(), StatusType.APPROVED);
                    return ItemResponseDto.create(lastBooking, nextBooking, item, comments);
                })
                .collect(Collectors.toList());

        log.info("Получен список всех Item для UserOwner с ID {}.", userOwnerId);
        return itemResponseDtos;
    }

    @Override
    public void deleteById(Long id, Long userOwnerId) {
        Item item = itemRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(ITEM_NOT_FOUND_ERROR));

        if (!userRepository.existsById(userOwnerId)) {
            throw new EntityNotFoundException(USER_NOT_FOUND_ERROR);
        }
        if (!item.getOwner().getId().equals(userOwnerId)) {
            log.info("Пользователь User с ID {} не является Owner.", userOwnerId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        if (itemRepository.existsById(id)) {
            log.info("Item c ID {} удалён UserOwner c ID {}.", id, userOwnerId);
            itemRepository.deleteItemByIdAndOwner_Id(id, userOwnerId);
        }
    }

    @Override
    public List<ItemDto> search(int from, int size, String text, Long userId) {
        userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND_ERROR));

        if (text == null || text.isBlank()) {
            log.info("Получен пустой лист поиска по запросу User ID {}.", userId);
            return List.of();
        }

        Pageable pageable = PageRequest.of(from == 0 ? 0 : (from / size), size);

        List<Item> items = itemRepository.findByText(text, pageable);
        log.info("Получен список всех Item  по запросу '{} 'для User с ID {}.", text, userId);
        return ItemMapper.listItemsToListDto(items);
    }

    @Transactional
    @Override
    public CommentResponseDto addComment(CommentDto commentDto, long itemId, long userId) {

        if (bookingRepository.findAllByBooker_IdAndItem_IdAndEndBefore(userId, itemId, LocalDateTime.now()).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Оставлять Comment может только User, у которого есть завершённый Booking для данного Item");
        }

        Comment comment = CommentMapper.dtoToComment(commentDto);
        comment.setAuthor(
                userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("Нет такого User.")));
        comment.setItem(
                itemRepository.findById(itemId).orElseThrow(() -> new EntityNotFoundException("Нет такого Item.")));
        comment = commentRepository.save(comment);
        log.info("Добавлен Comment для Item c id = {} от User с id = {}",
                comment.getItem().getId(), comment.getAuthor().getId());
        return CommentMapper.toResponseDto(comment);
    }
}
