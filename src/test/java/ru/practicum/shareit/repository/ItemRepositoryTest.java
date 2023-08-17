package ru.practicum.shareit.repository;

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@DataJpaTest
@Transactional
public class ItemRepositoryTest {

    User userOwner = User.builder()
            .id(10L)
            .name("Жора")
            .email("goraBoss@yandex.ru")
            .build();

    User userRequestor = User.builder()
            .id(11L)
            .name("Жора")
            .email("goraRequestor@yandex.ru")
            .build();

    ItemRequest itemRequest = ItemRequest.builder()
            .id(12L)
            .description("Нужен буравчик срочно")
            .requestor(userRequestor)
            .created(LocalDateTime.of(2023, 3, 3, 13, 13, 0))
            .build();
    Item item = Item.builder()
            .name("Буравчик")
            .description("Незаменимая вещь")
            .available(true)
            .owner(userOwner)
            .requestId(userRequestor.getId())
            .build();

}
