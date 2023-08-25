package ru.practicum.shareit.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.enums.StatusType;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@DisplayName("Тесты класса BookingDto")
public class BookingDtoTest {

    @Autowired
    private JacksonTester<BookingDto> jacksonTester;

    @Test
    @DisplayName("Тест на сериализацию класса BookingDto")
    @SneakyThrows
    void bookingDtoTest() {

        User user = User.builder()
                .id(1L)
                .name("Игорь")
                .email("Super@yandex.ru")
                .build();

        User booker = User.builder()
                .id(2L)
                .name("Игорь")
                .email("Super@yandex.ru")
                .build();

        Item item = Item.builder()
                .id(3L)
                .name("Молоток")
                .description("Описание")
                .available(true)
                .owner(user)
                .requestId(7L)
                .build();

        BookingDto bookingDto = BookingDto.builder()
                .id(1L)
                .start(LocalDateTime.of(2023, 1, 12, 12, 12))
                .end(LocalDateTime.of(2023, 1, 13, 12, 12))
                .item(item)
                .booker(booker)
                .status(StatusType.APPROVED)
                .build();

        JsonContent<BookingDto> bookingDtoJsonContent = jacksonTester.write(bookingDto);

        assertThat(bookingDtoJsonContent).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(bookingDtoJsonContent).extractingJsonPathStringValue("$.start").isEqualTo("2023-01-12T12:12:00");
        assertThat(bookingDtoJsonContent).extractingJsonPathStringValue("$.end").isEqualTo("2023-01-13T12:12:00");
        assertThat(bookingDtoJsonContent).extractingJsonPathNumberValue("$.item.id").isEqualTo(3);
        assertThat(bookingDtoJsonContent).extractingJsonPathStringValue("$.item.name").isEqualTo("Молоток");
        assertThat(bookingDtoJsonContent).extractingJsonPathStringValue("$.booker.name").isEqualTo("Игорь");
        assertThat(bookingDtoJsonContent).extractingJsonPathNumberValue("$.booker.id").isEqualTo(2);
        assertThat(bookingDtoJsonContent).extractingJsonPathStringValue("$.status").isEqualTo("APPROVED");
    }
}

