package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;


public class BookingMapper {
    private BookingMapper() {
    }

    public static BookingDto bookingToDto(Booking booking) {
        if (booking == null) {
            throw new IllegalArgumentException("Booking can`t be null");
        }

        return BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(booking.getItem())
                .status(booking.getStatus())
                .booker(booking.getBooker())
                .build();
    }

    public static Booking requestToBooking(BookingRequestDto bookingRequestDto) {
        return Booking.builder()
                .start(bookingRequestDto.getStart())
                .end(bookingRequestDto.getEnd())
                .build();
    }

    public static BookingRequestDto bookingToRequest(Booking booking) {
        return BookingRequestDto.builder()
                .start(booking.getStart())
                .end(booking.getEnd())
                .build();
    }

    public static Booking dtoToBooking(BookingDto bookingDto) {
        if (bookingDto == null) {
            throw new IllegalArgumentException("BookingDto can`t be null");
        }

        return Booking.builder()
                .id(bookingDto.getId())
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .item(bookingDto.getItem())
                .booker(bookingDto.getBooker())
                .status(bookingDto.getStatus())
                .build();
    }

    public static List<BookingDto> listBookingToListDto(Collection<Booking> bookings) {
        return bookings.stream().map(BookingMapper::bookingToDto).collect(Collectors.toList());
    }

    public static BookingResponseDto bookingToResponse(Booking booking) {
        return booking == null ? null : BookingResponseDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(ItemMapper.itemToDto(booking.getItem()))
                .booker(UserMapper.userToDto(booking.getBooker()))
                .status(booking.getStatus())
                .build();
    }

    public static BookingShortDto bookingToShort(Booking booking) {
        if (booking == null) {
            return null;
        }
        return BookingShortDto.builder()
                .id(booking.getId())
                .bookerId(booking.getBooker().getId())
                .build();
    }
}
