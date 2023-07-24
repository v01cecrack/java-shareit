package ru.practicum.shareit.user;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.ConflictException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserDao userDao;

    @Override
    public UserDto createUser(UserDto userDto) {
        validation(userDto);
        return userDao.createUser(userDto);
    }

    @Override
    public List<User> getUsers() {
        return userDao.getUsers();
    }

    @Override
    public void deleteUser(int userId) {
        userDao.deleteUser(userId);
    }

    @Override
    public User getUser(int userId) {
        return userDao.getUser(userId);
    }

    @Override
    public UserDto updateUser(int userId, UserDto userDto) {
        validationUpdate(userId, userDto);
        return userDao.updateUser(userId, userDto);
    }

    private void validation(UserDto userDto) {
        List<User> userList = userDao.getUsers();
        for (User user : userList) {
            if (user.getEmail().equals(userDto.getEmail())) {
                throw new ConflictException("Email already exist");
            }
        }
    }

    private void validationUpdate(int userId, UserDto userDto) {
        if (userDto.getEmail() == null) {
            return;
        }
        List<User> userList = userDao.getUsers();
        for (User user : userList) {
            if (user.getEmail().equals(userDto.getEmail()) && user.getId() != userId) {
                throw new ConflictException("User is not owner");
            }
        }
    }
}
