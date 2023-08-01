package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.error.ObjectNotFoundException;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @InjectMocks
    private UserServiceImpl userService;
    @Mock
    private UserRepository userRepository;
    private User expectedUser;

    @BeforeEach
    void setUser() {
        expectedUser = User.builder()
                .id(1)
                .name("Kostya")
                .email("kostya@yandex.ru")
                .build();
    }

    @Test
    void addUser_whenUserNameValid_thenSaveUser() {
        when(userRepository.save(expectedUser)).thenReturn(expectedUser);

        UserDto actualUser = userService.createUser(UserMapper.toUserDto(expectedUser));

        assertEquals(UserMapper.toUser(actualUser), expectedUser);
        verify(userRepository).save(expectedUser);
    }

    @Test
    void updateUserWhenUserFound() {
        when(userRepository.save(any(User.class))).thenReturn(expectedUser);
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(expectedUser));

        var returnedUser = userService.updateUser(UserMapper.toUserDto(expectedUser));

        assertThat(UserMapper.toUser(returnedUser), equalTo(expectedUser));
        verify(userRepository).save(expectedUser);
        verify(userRepository).findById(expectedUser.getId());
    }

    @Test
    void update_whenAddUserWithIncorrectId_thenThrowObjectNotFoundException() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());

        ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class, () ->
                userService.updateUser(UserMapper.toUserDto(expectedUser)));

        assertThat(exception.getMessage(), equalTo("Такой пользователь не существует!"));
        verify(userRepository, never()).save(expectedUser);
        verify(userRepository).findById(expectedUser.getId());
    }

    @Test
    void getAllUsers() {
        List<User> expectedUsers = List.of(expectedUser);
        List<UserDto> expectedUserDto = expectedUsers.stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());

        when(userRepository.findAll()).thenReturn(expectedUsers);

        List<UserDto> actualUsersDto = userService.getUsers();

        assertEquals(actualUsersDto.size(), 1);
        assertIterableEquals(expectedUserDto, actualUsersDto);
    }

    @Test
    void deleteUser_WhenUserExists_ShouldDeleteUser() {
        int userId = 1;

        when(userRepository.existsById(userId)).thenReturn(true);

        userService.deleteUser(userId);

        verify(userRepository, times(1)).deleteById(userId);
    }

    @Test
    void deleteWhenUserNotFound_thenThrowObjectNotFoundException() {

        ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class, () ->
                userService.deleteUser(expectedUser.getId()));

        assertThat(exception.getMessage(), equalTo("Такой пользователь не существует!"));
        verify(userRepository, never()).deleteById(expectedUser.getId());
    }

    @Test
    void getUserByIdWhenUserFound_ThenReturnUser() {
        when(userRepository.findById(expectedUser.getId())).thenReturn(Optional.of(expectedUser));
        UserDto actualUser = userService.getUser(expectedUser.getId());
        assertEquals(UserMapper.toUser(actualUser), expectedUser);
    }

    @Test
    void getUserByIdWhenUserNotFound_ThenObjectNotFoundException() {
        when(userRepository.findById(expectedUser.getId())).thenReturn(Optional.empty());

        ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class, () ->
                userService.getUser(expectedUser.getId()));

        assertThat(exception.getMessage(), equalTo("Такой пользователь не существует!"));
    }
}
