package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.EmailDuplicateException;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Transactional
    @Override
    public UserDto create(UserDto userDto) {
        try {
            log.info("UserDto {} создан.", userDto);
            return UserMapper.userToDto(userRepository.save(UserMapper.dtoToUser(userDto)));
        } catch (DataIntegrityViolationException e) {
            throw new EmailDuplicateException("Ошибка дубля Email.");
        }
    }

    @Transactional
    @Override
    public UserDto update(Long id, UserDto userDto) {
        try {
            User user = UserMapper.dtoToUser(getById(id));
            if (userDto.getName() != null && !userDto.getName().isBlank()) {
                user.setName(userDto.getName());
            }
            if (userDto.getEmail() != null && !userDto.getEmail().isBlank()) {
                user.setEmail(userDto.getEmail());
            }

            log.info("Обновлен пользователь c id = {}.", user.getId());
            return UserMapper.userToDto(userRepository.saveAndFlush(user));
        } catch (DataIntegrityViolationException e) {
            throw new EmailDuplicateException("Ошибка дубля Email");
        }
    }

    @Override
    public UserDto getById(Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            log.info("User c ID {} получен.", id);
            return UserMapper.userToDto(user.get());
        }
        log.info("User c ID {} не найден.", id);
        throw new EntityNotFoundException("User не найден");
    }


    @Override
    public List<UserDto> getAll() {
        List<User> users = userRepository.findAll();
        log.info("Все User получены.");
        return UserMapper.listUsersToListDto(users);
    }

    @Transactional
    @Override
    public void deleteById(Long id) {
        if (!userRepository.existsById(id)) {
            log.info("User c ID {} не найден.", id);
            throw new EntityNotFoundException("User не найден");
        }
        userRepository.deleteById(id);
        log.info("User c ID {} удалён.", id);
    }
}
