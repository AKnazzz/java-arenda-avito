package ru.practicum.shareit.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDto create(UserDto userDto) {
        return UserMapper.userToDto(userRepository.create(UserMapper.dtoToUser(userDto)));
    }

    @Override
    public UserDto update(Long id, UserDto userDto) {
        return UserMapper.userToDto(userRepository.update(id, UserMapper.dtoToUser(userDto)));
    }

    @Override
    public UserDto getById(Long id) {
        return UserMapper.userToDto(userRepository.getById(id));
    }

    @Override
    public List<UserDto> getAll() {
        return UserMapper.listUsersToListDto(userRepository.getAll());
    }

    @Override
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }
}
