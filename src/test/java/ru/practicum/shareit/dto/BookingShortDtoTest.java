package ru.practicum.shareit.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.test.annotation.Rollback;
import ru.practicum.shareit.booking.dto.BookingShortDto;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@DisplayName("Тесты класса BookingShortDto")
public class BookingShortDtoTest {

    @Autowired
    private JacksonTester<BookingShortDto> jacksonTester;

    @Test
    @DisplayName("Тест на сериализацию класса BookingShortDto")
    @SneakyThrows
    @Rollback(true)
    void bookingShortDtoTest() {

        BookingShortDto bookingShortDto = BookingShortDto.builder()
                .id(1L)
                .bookerId(1L)
                .build();

        JsonContent<BookingShortDto> bookingShortDtoJsonContent = jacksonTester.write(bookingShortDto);

        assertThat(bookingShortDtoJsonContent).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(bookingShortDtoJsonContent).extractingJsonPathNumberValue("$.bookerId").isEqualTo(1);
    }
}
