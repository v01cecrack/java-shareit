package ru.practicum.shareit.user.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Slf4j
public class UserDao {
    private int id = 0;
    private Map<Integer, User> userMap = new HashMap<>();

    public UserDto createUser(UserDto userDto) {
        userDto.setId(++id);
        User user = UserMapper.toUser(userDto);
        userMap.put(user.getId(), user);
        log.info("Пользователь: {} создан", user.getName());
        return userDto;
    }

    public List<User> getUsers() {
        List<User> userList = new ArrayList<>(userMap.values());
        log.info("Количество пользователей: {}", userMap.size());
        return userList;
    }

    public void deleteUser(int userId) {
        log.info("Пользователь удален");
        userMap.remove(userId);
    }

    public User getUser(int userId) {
        log.info("Получен пользователь: {}", userMap.get(userId));
        return userMap.get(userId);
    }

    public UserDto updateUser(int userId, UserDto userDto) {
        userDto.setId(userId);
        if (userDto.getEmail() == null) {
            userDto.setEmail(userMap.get(userId).getEmail());
        }
        if (userDto.getName() == null) {
            userDto.setName(userMap.get(userId).getName());
        }
        User user = UserMapper.toUser(userDto);
        userMap.put(userId, user);
        log.info("Пользователь обновлен");
        return userDto;
    }
}
