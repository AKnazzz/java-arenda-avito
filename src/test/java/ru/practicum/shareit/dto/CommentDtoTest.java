package ru.practicum.shareit.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.test.annotation.Rollback;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.user.model.User;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@DisplayName("Тесты класса CommentDto")
public class CommentDtoTest {

    @Autowired
    private JacksonTester<CommentDto> jacksonTester;


    @Test
    @DisplayName("Тест на сериализацию класса CommentDto")
    @SneakyThrows
    @Rollback(true)
    void commentDtoTest() {

        User user = User.builder()
                .id(1L)
                .name("Игорь")
                .email("Super@yandex.ru")
                .build();

        CommentDto commentDto = CommentDto.builder()
                .id(1L)
                .text("Всё хорошо")
                .author(user)
                .build();

        JsonContent<CommentDto> commentDtoJsonContent = jacksonTester.write(commentDto);

        assertThat(commentDtoJsonContent).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(commentDtoJsonContent).extractingJsonPathStringValue("$.text").isEqualTo("Всё хорошо");
        assertThat(commentDtoJsonContent).extractingJsonPathNumberValue("$.author.id").isEqualTo(1);
        assertThat(commentDtoJsonContent).extractingJsonPathStringValue("$.author.name").isEqualTo("Игорь");
        assertThat(commentDtoJsonContent).extractingJsonPathStringValue("$.author.email").isEqualTo("Super@yandex.ru");
    }

    @Test
    @DisplayName("Тест на десериализацию класса CommentDto")
    @SneakyThrows
    @Rollback(true)
    void commentDtoReadTest() {
        String comment
                = "{\"id\":1,\"text\":\"Всё хорошо\",\"author\":{\"id\":1,\"name\":\"Игорь\",\"email\":\"Super@yandex.ru\"}}";
        CommentDto commentDto = jacksonTester.parseObject(comment);

        assertThat(1L).isEqualTo(commentDto.getId());
        assertThat("Всё хорошо").isEqualTo(commentDto.getText());
        assertThat(1L).isEqualTo(commentDto.getAuthor().getId());
        assertThat("Игорь").isEqualTo(commentDto.getAuthor().getName());
        assertThat("Super@yandex.ru").isEqualTo(commentDto.getAuthor().getEmail());
    }


}
