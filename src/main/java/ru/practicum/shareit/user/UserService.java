package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto createUser(UserDto user);

    List<User> getUsers();

    void deleteUser(int userId);

    User getUser(int userId);

    UserDto updateUser(int userId, UserDto user);
}
