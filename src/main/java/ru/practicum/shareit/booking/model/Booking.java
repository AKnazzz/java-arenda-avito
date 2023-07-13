package ru.practicum.shareit.booking.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.model.enums.StatusType;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Booking {

    private Long id; // уникальный идентификатор бронирования;
    private LocalDateTime start; // дата и время начала бронирования;
    private LocalDateTime end; // дата и время конца бронирования;
    private Item item; // вещь, которую пользователь бронирует;
    private User booker; // пользователь, который осуществляет бронирование;
    private StatusType status; // статус бронирования.

}
