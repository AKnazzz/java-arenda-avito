package ru.practicum.shareit.booking.model.enums;

public enum StatusType {
    WAITING, // новое бронирование, ожидает одобрения
    APPROVED, // бронирование подтверждено владельцем
    REJECTED, // бронирование отклонено владельцем
    CANCELED // бронирование отменено создателем

}
