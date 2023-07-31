package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ItemMapper {

    private ItemMapper() {
    }

    public static ItemDto itemToDto(Item item) {
        if (item == null) {
            throw new IllegalArgumentException("Item can not be null.");
        }

        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .owner(item.getOwner())
                // .request(item.getRequest())
                .build();
    }

    public static ItemResponseDto toResposeItem(Item item) {
        if (item == null) {
            throw new IllegalArgumentException("Item can not be null.");
        }
        return ItemResponseDto.builder()
                .id(item.getId())
                .available(item.getAvailable())
                .description(item.getDescription())
                .name(item.getName())
                .build();
    }

    public static Item dtoToItem(ItemDto itemDto) {
        if (itemDto == null) {
            throw new IllegalArgumentException("ItemDto can not be null.");
        }

        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .owner(itemDto.getOwner())
                //  .request(itemDto.getRequest())
                .build();
    }

    public static List<ItemDto> listItemsToListDto(Collection<Item> items) {
        return items.stream().map(ItemMapper::itemToDto).collect(Collectors.toList());
    }

    public static List<ItemResponseDto> listItemsToListResponseDto(Collection<Item> items) {
        return items.stream().map(ItemMapper::toResposeItem).collect(Collectors.toList());
    }

    public static ItemDto itemDtoForOwner(Item item, List<Booking> bookings, List<Comment> comments) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .owner(item.getOwner())
                .lastBooking(bookings.isEmpty() || bookings.get(0) == null ? null : new BookingShortDto(
                        bookings.get(0).getId(), bookings.get(0).getBooker().getId()
                ))
                .nextBooking(bookings.size() <= 1 || bookings.get(1) == null ? null : new BookingShortDto(
                        bookings.get(1).getId(),
                        bookings.get(1).getBooker().getId()
                ))
                .comments(comments)//.stream().map(CommentMapper::commentToDto).collect(Collectors.toList()))
                .build();
    }

    public static List<ItemDto> itemDtoForOwner(
            List<Item> items,
            Map<Item, List<Booking>> bookings,
            Map<Item, List<Comment>> comments) {
        return items.stream()
                .map(item -> ItemMapper.itemDtoForOwner(
                        item,
                        bookings.getOrDefault(item, List.of()),
                        comments.getOrDefault(item, List.of()))
                )
                .collect(Collectors.toList());

    }
}



