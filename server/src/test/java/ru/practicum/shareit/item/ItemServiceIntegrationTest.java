package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class ItemServiceIntegrationTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserRepository userRepository;

    @Test
    void getUserItems_integrationTest() {
        User user = userRepository.save(new User(null, "Owner", "owner@mail.com"));
        assertThat(user).isNotNull();
    }
}