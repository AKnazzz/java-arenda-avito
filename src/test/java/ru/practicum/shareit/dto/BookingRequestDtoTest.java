package ru.practicum.shareit.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.test.annotation.Rollback;
import ru.practicum.shareit.booking.dto.BookingRequestDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@DisplayName("Тесты класса BookingRequestDto")
public class BookingRequestDtoTest {

    @Autowired
    private JacksonTester<BookingRequestDto> jacksonTester;

    @Test
    @DisplayName("Тест на сериализацию класса BookingRequestDto")
    @SneakyThrows
    @Rollback(true)
    void bookingRequestDtoTest() {

        BookingRequestDto bookingRequestDto = BookingRequestDto.builder()
                .itemId(1L)
                .start(LocalDateTime.of(2023, 1, 12, 12, 12))
                .end(LocalDateTime.of(2023, 1, 13, 12, 12))
                .build();

        JsonContent<BookingRequestDto> bookingRequestDtoJsonContent = jacksonTester.write(bookingRequestDto);


        assertThat(bookingRequestDtoJsonContent).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
        assertThat(bookingRequestDtoJsonContent).extractingJsonPathStringValue("$.start").isEqualTo("2023-01-12T12:12:00");
        assertThat(bookingRequestDtoJsonContent).extractingJsonPathStringValue("$.end").isEqualTo("2023-01-13T12:12:00");
    }

    @Test
    @DisplayName("Тест на десериализацию класса BookingRequestDto")
    @SneakyThrows
    @Rollback(true)
    void bookingRequestDtoReadTest() {
        String booking = "{\"itemId\": 1, \"start\": \"2023-01-12T12:12:00\", \"end\": \"2023-01-13T12:12:00\"}";
        BookingRequestDto bookingRequestDto = jacksonTester.parseObject(booking);

        assertThat(1L).isEqualTo(bookingRequestDto.getItemId());
        assertThat("2023-01-12T12:12").isEqualTo(bookingRequestDto.getStart().toString());
        assertThat("2023-01-13T12:12").isEqualTo(bookingRequestDto.getEnd().toString());
    }

}