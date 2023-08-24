package ru.practicum.shareit.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.test.annotation.Rollback;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@DisplayName("Тесты класса ItemRequestDto")
public class ItemRequestDtoTest {

    @Autowired
    private JacksonTester<ItemRequestDto> jacksonTester;


    @Test
    @DisplayName("Тест на сериализацию класса ItemRequestDto")
    @SneakyThrows
    @Rollback(true)
    void itemRequestDtoTest() {
        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .id(1L)
                .description("Нужна дрель")
                .created(LocalDateTime.of(2023, 5, 12, 12, 12, 0))
                .build();

        JsonContent<ItemRequestDto> itemRequestDtoJsonContent = jacksonTester.write(itemRequestDto);

        assertThat(itemRequestDtoJsonContent).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(itemRequestDtoJsonContent).extractingJsonPathStringValue("$.description").isEqualTo("Нужна дрель");
        assertThat(itemRequestDtoJsonContent).extractingJsonPathStringValue("$.created").isEqualTo("2023-05-12T12:12:00");
    }

    @Test
    @DisplayName("Тест на десериализацию класса ItemRequestDto")
    @SneakyThrows
    @Rollback(true)
    void itemRequestDtoReadTest() {
        String json = "{\"id\":1,\"description\":\"Нужна дрель\",\"created\":\"2023-05-12T12:12\"}";

        ItemRequestDto itemRequestDto = jacksonTester.parseObject(json);

        assertThat(1L).isEqualTo(itemRequestDto.getId());
        assertThat("Нужна дрель").isEqualTo(itemRequestDto.getDescription());
        assertThat("2023-05-12T12:12").isEqualTo(itemRequestDto.getCreated().toString());
    }

}
