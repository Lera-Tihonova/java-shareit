package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public List<UserDto> findAll() {
        return userRepository.findAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    public UserDto findById(Long id) {
        return userRepository.findById(id)
                .map(UserMapper::toUserDto)
                .orElseThrow(() -> new RuntimeException("Пользователь с id " + id + " не найден"));
    }

    public UserDto create(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        User createdUser = userRepository.create(user);
        return UserMapper.toUserDto(createdUser);
    }

    public UserDto update(Long id, UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        User updatedUser = userRepository.update(id, user);
        return UserMapper.toUserDto(updatedUser);
    }

    public void delete(Long id) {
        userRepository.delete(id);
    }
}