package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto createUser(UserDto user);

    List<UserDto> getUsers();

    void deleteUser(int userId);

    UserDto getUser(int userId);

    UserDto updateUser(UserDto userDto);
}
