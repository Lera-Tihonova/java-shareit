package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.DuplicateEmailException;
import ru.practicum.shareit.exception.NotFoundException;

import java.util.*;

@Repository
public class UserRepository {
    private final Map<Long, User> users = new HashMap<>();
    private long idCounter = 1;

    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    public Optional<User> findById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    public User create(User user) {
        if (isEmailExists(user.getEmail())) {
            throw new DuplicateEmailException("Пользователь с email " + user.getEmail() + " уже существует");
        }
        user.setId(idCounter++);
        users.put(user.getId(), user);
        return user;
    }

    public User update(Long id, User updatedUser) {
        User existingUser = findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + id + " не найден"));

        if (updatedUser.getEmail() != null && !updatedUser.getEmail().equals(existingUser.getEmail())) {
            if (isEmailExists(updatedUser.getEmail())) {
                throw new DuplicateEmailException("Пользователь с email " + updatedUser.getEmail() + " уже существует");
            }
            existingUser.setEmail(updatedUser.getEmail());
        }

        if (updatedUser.getName() != null) {
            existingUser.setName(updatedUser.getName());
        }

        return existingUser;
    }

    public void delete(Long id) {
        users.remove(id);
    }

    private boolean isEmailExists(String email) {
        return users.values().stream()
                .anyMatch(user -> user.getEmail().equals(email));
    }
}