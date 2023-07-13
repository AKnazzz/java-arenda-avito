package ru.practicum.shareit.user.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.EmailDuplicateException;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class InMemoryUserRepository implements UserRepository {

    private final Map<Long, User> users = new HashMap<>();
    private Long id = 1L;

    @Override
    public User create(User user) {
        duplicateCheck(user);
        user.setId(id);
        users.put(id++, user);
        log.info("User c ID {} создан.", user.getId());
        return user;
    }

    @Override
    public User update(Long id, User user) {
        if (users.containsKey(id)) {
            User oldUser = users.get(id);
            if (user.getName() != null && !user.getName().isBlank()) {
                oldUser.setName(user.getName());
            }
            if (user.getEmail() != null && !user.getEmail().isBlank() && !user.getEmail().equals(oldUser.getEmail())) {
                duplicateCheck(user);
                oldUser.setEmail(user.getEmail());

            }
            log.info("Данные User с ID {} обновлёны.", id);
            return oldUser;
        }
        log.info("User с ID {} не найден .", id);
        throw new EntityNotFoundException("User ID " + id + " не найден.");
    }

    @Override
    public User getById(Long id) {
        if (users.containsKey(id)) {
            User user = users.get(id);
            log.info("User с ID {} получен .", id);
            return user;
        }
        log.info("User с ID {} не найден.", id);
        throw new EntityNotFoundException("User ID " + id + " не найден.");
    }

    @Override
    public List<User> getAll() {
        log.info("Список всех существующих User получен.");
        return new ArrayList<>(users.values());
    }

    @Override
    public void deleteById(Long id) {
        if (users.containsKey(id)) {
            users.remove(id);
            log.info("User с ID {} удалён.", id);
        } else {
            log.info("User с ID {} не найден.", id);
            throw new EntityNotFoundException("User ID " + id + " не найден.");
        }
    }

    private void duplicateCheck(User user) {
        for (User value : users.values()) {
            if (user.getEmail().equals(value.getEmail())) {
                throw new EmailDuplicateException("Некорректный запрос. Данный Email уже существует.");
            }
        }
    }

}
