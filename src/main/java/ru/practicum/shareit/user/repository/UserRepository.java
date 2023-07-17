package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserRepository {

    User create(User user);

    User update(Long id, User user);

    User getById(Long id);

    List<User> getAll();

    void deleteById(Long id);

    void userExistValidation(Long id);
}
