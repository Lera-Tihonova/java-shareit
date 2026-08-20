package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserClient userClient;

    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        log.info("GET /users");
        return userClient.getAllUsers();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUserById(@PathVariable Long userId) {
        log.info("GET /users/{}", userId);
        return userClient.getUserById(userId);
    }

    @PostMapping
    public ResponseEntity<Object> createUser(@Valid @RequestBody UserDto userDto) {
        log.info("POST /users");
        return userClient.createUser(userDto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@PathVariable Long userId, @RequestBody UserDto userDto) {
        log.info("PATCH /users/{}", userId);
        return userClient.updateUser(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUser(@PathVariable Long userId) {
        log.info("DELETE /users/{}", userId);
        return userClient.deleteUser(userId);
    }
}