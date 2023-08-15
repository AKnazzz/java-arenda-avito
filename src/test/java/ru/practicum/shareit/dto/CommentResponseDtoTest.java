package ru.practicum.shareit.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.test.annotation.Rollback;
import ru.practicum.shareit.item.dto.CommentResponseDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@DisplayName("Тесты класса CommentResponseDto")
public class CommentResponseDtoTest {

    @Autowired
    private JacksonTester<CommentResponseDto> jacksonTester;

    @Test
    @DisplayName("Тест на сериализацию класса CommentResponseDto")
    @SneakyThrows
    @Rollback(true)
    void commentResponseDtoTest() {

        CommentResponseDto commentResponseDto = CommentResponseDto.builder()
                .id(1L)
                .text("Всё хорошо")
                .authorName("Иван")
                .created(LocalDateTime.of(2023, 7, 7, 12, 12))
                .build();

        JsonContent<CommentResponseDto> commentResponseDtoJsonContent = jacksonTester.write(commentResponseDto);

        assertThat(commentResponseDtoJsonContent).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(commentResponseDtoJsonContent).extractingJsonPathStringValue("$.text").isEqualTo("Всё хорошо");
        assertThat(commentResponseDtoJsonContent).extractingJsonPathStringValue("$.authorName").isEqualTo("Иван");
    }

    @Test
    @DisplayName("Тест на десериализацию класса CommentResponseDto")
    @SneakyThrows
    @Rollback(true)
    void commentDtoReadTest() {
        String comment
                = "{\"id\":1,\"text\":\"Всё хорошо\",\"authorName\":\"Иван\",\"created\":\"2023-07-07T12:12\"}";
        CommentResponseDto commentResponseDto = jacksonTester.parseObject(comment);

        assertThat(1L).isEqualTo(commentResponseDto.getId());
        assertThat("Всё хорошо").isEqualTo(commentResponseDto.getText());
        assertThat("Иван").isEqualTo(commentResponseDto.getAuthorName());
        assertThat("2023-07-07T12:12").isEqualTo(commentResponseDto.getCreated().toString());


    }

}
