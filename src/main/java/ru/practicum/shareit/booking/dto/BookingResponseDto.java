package ru.practicum.shareit.booking.dto;

import lombok.*;
import ru.practicum.shareit.booking.model.enums.StatusType;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@EqualsAndHashCode
@NoArgsConstructor
public class BookingResponseDto {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private ItemDto item;
    private UserDto booker;
    private StatusType status;
}
