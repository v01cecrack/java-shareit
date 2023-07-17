package ru.practicum.shareit.user;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.ConflictException;
import ru.practicum.shareit.error.ObjectNotFoundException;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
//    private final UserDao userDao;

    @Override
    public UserDto createUser(UserDto userDto) {
        //validation(userDto);
        User user = UserMapper.toUser(userDto);
        return UserMapper.toUserDto(userRepository.save(user));
    }

    @Override
    public List<User> getUsers() {
        return userRepository.findAll();
//        return userDao.getUsers();
    }

    @Override
    public void deleteUser(int userId) {
        userRepository.deleteById(userId);
    }

    @Override
    public User getUser(int userId) {
        return userRepository.findById(userId).orElseThrow(() -> new ObjectNotFoundException("no user with such id"));
//        return userRepository.findById(userId).get();
    }

    @Override
    public UserDto updateUser(int userId, UserDto userDto) {
//        validationUpdate(userId, userDto);
        User user = UserMapper.toUser(userDto);
        user.setId(userId);
        User obj = userRepository.findById(userId).get();
        Optional.ofNullable(user.getName()).ifPresent(obj::setName);
        Optional.ofNullable(user.getEmail()).ifPresent(obj::setEmail);
        return UserMapper.toUserDto(userRepository.save(obj));
    }

    private void validation(UserDto userDto) {
        List<User> users = userRepository.findAll();
        for (User user : users) {
            if (user.getEmail().equals(userDto.getEmail())) {
                throw new ConflictException("Email already exist");
            }
        }
    }

    private void validationUpdate(int userId, UserDto userDto) {
        if (userDto.getEmail() == null) {
            return;
        }
        List<User> users = userRepository.findAll();
        for (User user : users) {
            if (user.getEmail().equals(userDto.getEmail()) && user.getId() != userId) {
                throw new ConflictException("User is not owner");
            }
        }
    }
}
