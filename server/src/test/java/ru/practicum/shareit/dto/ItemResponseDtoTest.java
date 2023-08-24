package ru.practicum.shareit.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.test.annotation.Rollback;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.enums.StatusType;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@DisplayName("Тесты класса ItemResponseDto")
public class ItemResponseDtoTest {


    @Autowired
    private JacksonTester<ItemResponseDto> jacksonTester;

    @Test
    @DisplayName("Тест на сериализацию класса ItemResponseDto")
    @SneakyThrows
    @Rollback(true)
    void itemResponseDtoTest() {

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

        Comment comment = Comment.builder()
                .id(4L)
                .author(booker)
                .text("Всё понравилось")
                .item(item)
                .created(LocalDateTime.of(2023, 7, 07, 12, 12))
                .build();

        Booking last = Booking.builder()
                .id(5L)
                .start(LocalDateTime.of(2023, 1, 12, 12, 12))
                .end(LocalDateTime.of(2023, 1, 13, 12, 12))
                .item(item)
                .booker(booker)
                .status(StatusType.APPROVED)
                .build();

        Booking next = Booking.builder()
                .id(6L)
                .start(LocalDateTime.of(2023, 10, 12, 12, 12))
                .end(LocalDateTime.of(2023, 10, 13, 12, 12))
                .item(item)
                .booker(booker)
                .status(StatusType.APPROVED)
                .build();

        ItemResponseDto itemResponseDto = ItemResponseDto.create(last, next, item, List.of(comment));
        JsonContent<ItemResponseDto> itemResponseDtoJsonContent = jacksonTester.write(itemResponseDto);


        assertThat(itemResponseDtoJsonContent).hasJsonPath("$.id");
        assertThat(itemResponseDtoJsonContent).hasJsonPath("$.name");
        assertThat(itemResponseDtoJsonContent).hasJsonPath("$.description");
        assertThat(itemResponseDtoJsonContent).hasJsonPath("$.available");
        assertThat(itemResponseDtoJsonContent).hasJsonPath("$.lastBooking.id");
        assertThat(itemResponseDtoJsonContent).hasJsonPath("$.lastBooking.bookerId");
        assertThat(itemResponseDtoJsonContent).hasJsonPath("$.nextBooking.id");
        assertThat(itemResponseDtoJsonContent).hasJsonPath("$.nextBooking.bookerId");
        assertThat(itemResponseDtoJsonContent).hasJsonPath("$.requestId");
        assertThat(itemResponseDtoJsonContent).hasJsonPath("$.comments");
        assertThat(itemResponseDtoJsonContent).extractingJsonPathNumberValue("$.id").isEqualTo(3);
        assertThat(itemResponseDtoJsonContent).extractingJsonPathStringValue("$.name").isEqualTo("Молоток");
        assertThat(itemResponseDtoJsonContent).extractingJsonPathStringValue("$.description").isEqualTo("Описание");
        assertThat(itemResponseDtoJsonContent).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
        assertThat(itemResponseDtoJsonContent).extractingJsonPathNumberValue("$.lastBooking.id").isEqualTo(5);
        assertThat(itemResponseDtoJsonContent).extractingJsonPathNumberValue("$.lastBooking.bookerId").isEqualTo(2);
        assertThat(itemResponseDtoJsonContent).extractingJsonPathNumberValue("$.nextBooking.id").isEqualTo(6);
        assertThat(itemResponseDtoJsonContent).extractingJsonPathNumberValue("$.nextBooking.bookerId").isEqualTo(2);
        assertThat(itemResponseDtoJsonContent).extractingJsonPathNumberValue("$.requestId").isEqualTo(7);
        assertThat(itemResponseDtoJsonContent).extractingJsonPathArrayValue("$.comments").isNotNull();
    }

}
