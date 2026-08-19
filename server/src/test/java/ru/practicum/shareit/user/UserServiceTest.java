package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.DuplicateEmailException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void findAll_shouldReturnAllUsers() {
        List<User> users = List.of(
                new User(1L, "User1", "user1@mail.com"),
                new User(2L, "User2", "user2@mail.com")
        );
        when(userRepository.findAll()).thenReturn(users);
        List<UserDto> result = userService.getAllUsers();
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("User1");
    }

    @Test
    void findById_shouldReturnUser() {
        User user = new User(1L, "User1", "user1@mail.com");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        UserDto result = userService.getUserById(1L);
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("User1");
    }

    @Test
    void findById_shouldThrowNotFoundException_whenUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> userService.getUserById(1L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Пользователь с id 1 не найден");
    }

    @Test
    void create_shouldCreateUser() {
        UserDto dto = new UserDto(null, "New User", "new@mail.com");
        User user = new User(1L, "New User", "new@mail.com");
        when(userRepository.existsByEmail("new@mail.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(user);
        UserDto result = userService.createUser(dto);
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void create_shouldThrowDuplicateEmailException_whenEmailExists() {
        UserDto dto = new UserDto(null, "New User", "existing@mail.com");
        when(userRepository.existsByEmail("existing@mail.com")).thenReturn(true);
        assertThatThrownBy(() -> userService.createUser(dto))
                .isInstanceOf(DuplicateEmailException.class)
                .hasMessageContaining("Пользователь с email existing@mail.com уже существует");
    }

    @Test
    void update_shouldUpdateUser() {
        User existing = new User(1L, "Old Name", "old@mail.com");
        UserDto dto = new UserDto(null, "New Name", null);
        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(userRepository.save(any(User.class))).thenReturn(existing);
        UserDto result = userService.updateUser(1L, dto);
        assertThat(result.getName()).isEqualTo("New Name");
    }

    @Test
    void delete_shouldDeleteUser() {
        when(userRepository.existsById(1L)).thenReturn(true);
        doNothing().when(userRepository).deleteById(1L);
        userService.deleteUser(1L);
        verify(userRepository).deleteById(1L);
    }
}