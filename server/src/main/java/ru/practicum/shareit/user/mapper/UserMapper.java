package ru.practicum.shareit.user.mapper;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class UserMapper {

    private UserMapper() {
    }

    public static UserDto userToDto(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User can not be null.");
        }

        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public static User dtoToUser(UserDto userDto) {
        if (userDto == null) {
            throw new IllegalArgumentException("UserDto can not be null.");
        }
        return User.builder()
                .id(userDto.getId())
                .name(userDto.getName())
                .email(userDto.getEmail())
                .build();
    }

    public static List<UserDto> listUsersToListDto(Collection<User> users) {
        return users.stream().map(UserMapper::userToDto).collect(Collectors.toList());
    }
}
