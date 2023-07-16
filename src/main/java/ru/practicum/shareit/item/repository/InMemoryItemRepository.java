package ru.practicum.shareit.item.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class InMemoryItemRepository implements ItemRepository {

    private final Map<Long, Item> items = new HashMap<>();
    private Long id = 1L;

    private final UserRepository userRepository;

    @Autowired

    public InMemoryItemRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Item create(Item item, Long userOwnerId) {
        if (item.getAvailable() == null || !item.getAvailable()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Item не доступен для share");
        }
        User user = userRepository.getById(userOwnerId);
        item.setId(id);
        item.setOwner(user);
        items.put(id++, item);
        log.info("User(Owner) c ID {} создал Item c ID {}.", userOwnerId, item.getId());
        return item;
    }

    @Override
    public Item update(Long id, Item item, Long userOwnerId) {
        itemExistValidation(id);
        Item oldItem = items.get(id);
        if (items.containsKey(id) && items.get(id).getOwner().equals(userRepository.getById(userOwnerId))) {
            if (item.getName() != null && !item.getName().isBlank()) {
                oldItem.setName(item.getName());
            }
            if (item.getDescription() != null && !item.getDescription().isBlank()) {
                oldItem.setDescription(item.getDescription());
            }
            if (item.getAvailable() != null) {
                oldItem.setAvailable(item.getAvailable());
            }
            log.info("User(Owner) c ID {} обновил данные Item c ID {}.", userOwnerId, item.getId());
        } else if (!items.containsKey(id) && items.get(id).getOwner().equals(userRepository.getById(userOwnerId))) {
            log.info("Item с ID {} не найден.", id);
            throw new EntityNotFoundException("Item с ID не найден.");
        } else {
            log.info("User с ID {} не является owner.", userOwnerId);
            throw new ResponseStatusException(HttpStatus.valueOf(404), "User с ID {} не является owner.");
        }
        return oldItem;
    }

    @Override
    public Item getById(Long id, Long userId) {
        itemExistValidation(id);
        userRepository.userExistValidation(userId);
        Item item = items.get(id);
        log.info("User c ID {} получил данные Item c ID {}.", userId, id);
        return item;
    }

    @Override
    public List<Item> getAllByOwner(Long userOwnerId) {
        userRepository.userExistValidation(userOwnerId);
        List<Item> list = items.values().stream()
                .filter(item -> item.getOwner().getId().equals(userOwnerId))
                .collect(Collectors.toList());
        log.info("User(Owner) c ID {} получил список всех своих Item.", userOwnerId);
        return list;
    }

    @Override
    public void deleteById(Long id, Long userOwnerId) {
        itemExistValidation(id);
        userRepository.userExistValidation(userOwnerId);
        if (items.containsKey(id) && items.get(id).getOwner().equals(userRepository.getById(userOwnerId))) {
            items.remove(id);
            log.info("User(Owner) с ID {} удалил  Item c ID {}.", userOwnerId, id);
        } else if (!items.containsKey(id) && items.get(id).getOwner().equals(userRepository.getById(userOwnerId))) {
            log.info("Item с ID {} не найден.", id);
            throw new EntityNotFoundException("Item с ID не найден.");
        } else {
            log.info("User с ID {} не является owner.", userOwnerId);
            throw new ResponseStatusException(HttpStatus.valueOf(404), "User с ID {} не является owner.");
        }
    }

    @Override
    public List<Item> search(String text, Long userId) {
        userRepository.userExistValidation(userId);
        if (text == null || text.isBlank()) {
            log.info("Получен пустой лист поиска по запросу User ID {}.", userId);
            return List.of();
        }
        List<Item> itemsList = items.values().stream()
                .filter(a -> (a.getDescription().toLowerCase().contains(text.toLowerCase()) || a.getName().toLowerCase()
                        .contains(text.toLowerCase())) && a.getAvailable())
                .collect(Collectors.toList());
        log.info("User с ID {} получил результаты поиска по запросу {}.", userId, text);
        return itemsList;
    }

    public void itemExistValidation (long id){
        if (!items.containsKey(id)) {
            log.info("Item с ID {} не найден.", id);
            throw new EntityNotFoundException("Item ID " + id + " не найден.");
        }
    }
}
