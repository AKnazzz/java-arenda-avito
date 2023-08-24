package ru.practicum.shareit.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.test.annotation.Rollback;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.enums.StatusType;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@DisplayName("Тесты класса BookingResponseDto")
public class BookingResponseDtoTest {

    @Autowired
    private JacksonTester<BookingResponseDto> jacksonTester;

    @Test
    @DisplayName("Тест на сериализацию класса BookingResponseDto")
    @SneakyThrows
    @Rollback(true)
    void bookingResponseDtoTest() {
        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("Игорь")
                .email("Super@yandex.ru")
                .build();

        BookingShortDto last = BookingShortDto.builder()
                .id(4L)
                .bookerId(4L)
                .build();

        BookingShortDto next = BookingShortDto.builder()
                .id(5L)
                .bookerId(5L)
                .build();


        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("Утюг")
                .description("Полное описание")
                .available(true)
                .requestId(3L)
                .lastBooking(last)
                .nextBooking(next)
                .comments(null)
                .build();

        BookingResponseDto bookingResponseDto = BookingResponseDto.builder()
                .id(1L)
                .start(LocalDateTime.of(2023, 1, 12, 12, 12))
                .end(LocalDateTime.of(2023, 1, 13, 12, 12))
                .item(itemDto)
                .booker(userDto)
                .status(StatusType.APPROVED)
                .build();

        JsonContent<BookingResponseDto> bookingResponseDtoJsonContent = jacksonTester.write(bookingResponseDto);

        assertThat(bookingResponseDtoJsonContent).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat("2023-01-12T12:12").isEqualTo(bookingResponseDto.getStart().toString());
        assertThat("2023-01-13T12:12").isEqualTo(bookingResponseDto.getEnd().toString());
        assertThat(bookingResponseDtoJsonContent).extractingJsonPathNumberValue("$.item.id").isEqualTo(1);
        assertThat(bookingResponseDtoJsonContent).extractingJsonPathStringValue("$.item.name").isEqualTo("Утюг");
        assertThat(bookingResponseDtoJsonContent).extractingJsonPathStringValue("$.item.description").isEqualTo("Полное описание");
        assertThat(bookingResponseDtoJsonContent).extractingJsonPathBooleanValue("$.item.available").isEqualTo(true);
        assertThat(bookingResponseDtoJsonContent).extractingJsonPathNumberValue("$.item.requestId").isEqualTo(3);
        assertThat(bookingResponseDtoJsonContent).extractingJsonPathNumberValue("$.item.lastBooking.id").isEqualTo(4);
        assertThat(bookingResponseDtoJsonContent).extractingJsonPathNumberValue("$.item.lastBooking.bookerId").isEqualTo(4);
        assertThat(bookingResponseDtoJsonContent).extractingJsonPathNumberValue("$.item.nextBooking.bookerId").isEqualTo(5);
        assertThat(bookingResponseDtoJsonContent).extractingJsonPathNumberValue("$.item.nextBooking.id").isEqualTo(5);
        assertThat(bookingResponseDtoJsonContent).extractingJsonPathArrayValue("$.item.comments").isNull();
        assertThat(bookingResponseDtoJsonContent).extractingJsonPathStringValue("$.status").isEqualTo("APPROVED");
    }

}


