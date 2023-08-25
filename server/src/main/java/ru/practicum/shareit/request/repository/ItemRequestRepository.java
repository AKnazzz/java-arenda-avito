package ru.practicum.shareit.request.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.Collection;
import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    List<ItemRequest> findAllByRequestor_idOrderByCreatedAsc(Long userId); // возвращает список запросов конкретного
    // пользователя отсортированных по дате создания (по возрастанию)

    Page<ItemRequest> findAllByRequestor_IdNotIn(Collection<Long> ownerId, Pageable pageable); // возвращает Page с параметрами
    // заданными Pageable по всем пользователям



}
