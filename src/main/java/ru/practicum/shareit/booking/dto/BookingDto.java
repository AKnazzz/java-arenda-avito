package ru.practicum.shareit.booking.dto;

import lombok.*;
import ru.practicum.shareit.booking.model.enums.StatusType;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;


@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookingDto {
    private Long id;
    @NotNull(message = "Не указана дата начала бронирования")
    private LocalDateTime start;
    @NotNull(message = "Не указана дата окончания бронирования")
    private LocalDateTime end;
    private Item item;
    private User booker;
    private StatusType status;
}
