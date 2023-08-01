package ru.practicum.shareit.user;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.ObjectNotFoundException;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    public List<UserDto> getUsers() {

        List<User> users = userRepository.findAll();
        return users.stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteUser(int userId) {
        if (!userRepository.existsById(userId)) {
            throw new ObjectNotFoundException("Такой пользователь не существует!");
        }
        userRepository.deleteById(userId);
    }

    @Override
    public UserDto getUser(int userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ObjectNotFoundException("Такой пользователь не существует!"));
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto updateUser(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        User obj = userRepository.findById(user.getId()).orElseThrow(() -> new ObjectNotFoundException("Такой пользователь не существует!"));
        Optional.ofNullable(user.getName()).ifPresent(obj::setName);
        Optional.ofNullable(user.getEmail()).ifPresent(obj::setEmail);
        return UserMapper.toUserDto(userRepository.save(obj));
    }
}
