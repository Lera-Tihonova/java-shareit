package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class ItemRequestRepositoryTest {

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByRequestorId_shouldReturnRequests() {
        User requestor = userRepository.save(new User(null, "Requestor", "requestor@mail.com"));
        User otherUser = userRepository.save(new User(null, "Other", "other@mail.com"));

        ItemRequest request1 = new ItemRequest(null, "Нужна дрель", requestor, LocalDateTime.now());
        ItemRequest request2 = new ItemRequest(null, "Нужна отвертка", requestor, LocalDateTime.now());
        ItemRequest request3 = new ItemRequest(null, "Нужна пила", otherUser, LocalDateTime.now());

        itemRequestRepository.save(request1);
        itemRequestRepository.save(request2);
        itemRequestRepository.save(request3);

        Sort sort = Sort.by(Sort.Direction.DESC, "created");
        List<ItemRequest> requests = itemRequestRepository.findByRequestorId(requestor.getId(), sort);

        assertThat(requests).hasSize(2);
        assertThat(requests).extracting(ItemRequest::getDescription)
                .containsExactlyInAnyOrder("Нужна дрель", "Нужна отвертка");
    }

    @Test
    void findAllOther_shouldReturnRequestsFromOtherUsers() {
        User requestor = userRepository.save(new User(null, "Requestor", "requestor@mail.com"));
        User otherUser1 = userRepository.save(new User(null, "Other1", "other1@mail.com"));
        User otherUser2 = userRepository.save(new User(null, "Other2", "other2@mail.com"));

        ItemRequest request1 = new ItemRequest(null, "Нужна дрель", requestor, LocalDateTime.now());
        ItemRequest request2 = new ItemRequest(null, "Нужна отвертка", otherUser1, LocalDateTime.now());
        ItemRequest request3 = new ItemRequest(null, "Нужна пила", otherUser2, LocalDateTime.now());

        itemRequestRepository.save(request1);
        itemRequestRepository.save(request2);
        itemRequestRepository.save(request3);

        PageRequest pageRequest = PageRequest.of(0, 10);
        List<ItemRequest> requests = itemRequestRepository.findAllOther(requestor.getId(), pageRequest);

        assertThat(requests).hasSize(2);
        assertThat(requests).extracting(ItemRequest::getDescription)
                .containsExactlyInAnyOrder("Нужна отвертка", "Нужна пила");
    }

    @Test
    void findAllOther_shouldReturnEmptyList_whenNoOtherRequests() {
        User requestor = userRepository.save(new User(null, "Requestor", "requestor@mail.com"));

        ItemRequest request1 = new ItemRequest(null, "Нужна дрель", requestor, LocalDateTime.now());
        itemRequestRepository.save(request1);

        PageRequest pageRequest = PageRequest.of(0, 10);
        List<ItemRequest> requests = itemRequestRepository.findAllOther(requestor.getId(), pageRequest);

        assertThat(requests).isEmpty();
    }
}