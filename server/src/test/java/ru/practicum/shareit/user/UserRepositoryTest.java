package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void existsByEmail_shouldReturnTrue_whenEmailExists() {
        User user = userRepository.save(new User(null, "Test User", "test@mail.com"));
        boolean exists = userRepository.existsByEmail("test@mail.com");
        assertThat(exists).isTrue();
    }

    @Test
    void existsByEmail_shouldReturnFalse_whenEmailNotExists() {
        boolean exists = userRepository.existsByEmail("nonexistent@mail.com");
        assertThat(exists).isFalse();
    }
}