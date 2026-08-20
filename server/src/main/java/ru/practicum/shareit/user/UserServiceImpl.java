package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.DuplicateEmailException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getUserById(Long userId) {
        return UserMapper.toDto(userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден")));
    }

    @Override
    @Transactional
    public UserDto createUser(UserDto userDto) {
        checkEmailDuplicate(userDto.getEmail());
        User user = UserMapper.toEntity(userDto);
        return UserMapper.toDto(userRepository.save(user));
    }

    @Override
    @Transactional
    public UserDto updateUser(Long userId, UserDto userDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));

        if (userDto.getEmail() != null) {
            checkEmailDuplicate(userDto.getEmail());
            user.setEmail(userDto.getEmail());
        }
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }

        return UserMapper.toDto(userRepository.save(user));
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь с id " + userId + " не найден");
        }
        userRepository.deleteById(userId);
    }

    private void checkEmailDuplicate(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new DuplicateEmailException("Пользователь с email " + email + " уже существует");
        }
    }
}