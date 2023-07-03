package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@Slf4j
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    public UserDto createUser(@Valid @RequestBody UserDto userDto) {
        log.info("Получен POST-запрос: /users на создание пользователя: {}", userDto);
        return userService.createUser(userDto);
    }

    @GetMapping
    public List<User> getUsers() {
        log.info("Получен GET-запрос: /users на получение всех пользователей");
        return userService.getUsers();
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable int userId) {
        log.info("Получен DELETE-запрос: /users/{id} на удаление пользователя с ID = {}", userId);
        userService.deleteUser(userId);
    }

    @GetMapping("/{userId}")
    public User getUser(@PathVariable int userId) {
        log.info("Получен GET-запрос: /users/{id} на получение пользователя с ID = {}", userId);
        return userService.getUser(userId);
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@PathVariable int userId, @RequestBody UserDto userDto) {
        log.info("Получен PATCH-запрос: /users/{id} на обновление пользователя с ID = {}", userId);
        return userService.updateUser(userId, userDto);
    }
}
