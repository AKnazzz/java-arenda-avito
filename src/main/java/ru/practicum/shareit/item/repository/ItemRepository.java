package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findAllByOwnerIdOrderByIdAsc(Long ownerId); // все Item, принадлежат владельцу с заданным ID,
    // отсортированные по возрастанию их ID.

    @Query("select it "
            + "from Item it "
            + "where it.available = true "
            + "and (lower (it.name) like concat('%', lower(?1), '%') "
            + "or lower (it.description) like concat('%', lower(?1), '%')) ")
    List<Item> findByText(String text); // для поиска item по тексту (части запроса)

    void deleteItemByIdAndOwner_Id(long itemId, long userId); // удаление хозяином вещи своей вещи
}
