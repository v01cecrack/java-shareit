package ru.practicum.shareit.user;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.ObjectNotFoundException;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserDto createUser(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        return UserMapper.toUserDto(userRepository.save(user));
    }

    @Override
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @Override
    public void deleteUser(int userId) {
        userRepository.deleteById(userId);
    }

    @Override
    public User getUser(int userId) {
        return userRepository.findById(userId).orElseThrow(() -> new ObjectNotFoundException("no user with such id"));
    }

    @Override
    public UserDto updateUser(int userId, UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        user.setId(userId);
        User obj = userRepository.findById(userId).get();
        Optional.ofNullable(user.getName()).ifPresent(obj::setName);
        Optional.ofNullable(user.getEmail()).ifPresent(obj::setEmail);
        return UserMapper.toUserDto(userRepository.save(obj));
    }
}
