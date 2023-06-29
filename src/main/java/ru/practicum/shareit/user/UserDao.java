package ru.practicum.shareit.user;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import java.util.*;

@Repository
@Data
public class UserDao {
    private Map<Integer, User> userMap = new HashMap<>();

    public UserDto createUser(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        userMap.put(user.getId(),user);
        return userDto;
    }

    public List<User> getUsers() {
        List<User> userList = new ArrayList<>(userMap.values());
        return userList;
    }

    public void deleteUser(int userId) {
        userMap.remove(userId);
    }

    public User getUser(int userId) {
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
        userMap.remove(userId);
        userMap.put(userId, user);
        return userDto;
    }
}
