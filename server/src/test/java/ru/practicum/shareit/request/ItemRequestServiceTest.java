package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceTest {

    @Mock
    private ItemRequestRepository requestRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    @InjectMocks
    private ItemRequestService requestService;

    @Test
    void create_shouldCreateRequest() {
        User user = new User(1L, "User", "user@mail.com");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(requestRepository.save(any(ItemRequest.class))).thenAnswer(invocation -> {
            ItemRequest r = invocation.getArgument(0);
            r.setId(1L);
            return r;
        });

        ItemRequestDto result = requestService.create(1L, "Нужна дрель");
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getDescription()).isEqualTo("Нужна дрель");
    }

    @Test
    void create_shouldThrowNotFoundException_whenUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> requestService.create(1L, "Нужна дрель"))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Пользователь с id 1 не найден");
    }

    @Test
    void findByRequestor_shouldReturnRequests() {
        User user = new User(1L, "User", "user@mail.com");
        ItemRequest request = new ItemRequest(1L, "Нужна дрель", user, LocalDateTime.now());
        List<ItemRequest> requests = List.of(request);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(requestRepository.findByRequestorId(1L, Sort.by(Sort.Direction.DESC, "created")))
                .thenReturn(requests);

        List<ItemRequestDto> result = requestService.findByRequestor(1L);
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDescription()).isEqualTo("Нужна дрель");
    }

    @Test
    void findAllOther_shouldReturnRequestsFromOtherUsers() {
        User user = new User(1L, "User", "user@mail.com");
        User otherUser = new User(2L, "Other", "other@mail.com");
        ItemRequest request = new ItemRequest(1L, "Нужна дрель", otherUser, LocalDateTime.now());
        List<ItemRequest> requests = List.of(request);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(requestRepository.findAllOther(any(Long.class), any(PageRequest.class)))
                .thenReturn(requests);

        List<ItemRequestDto> result = requestService.findAllOther(1L, 0L, 10);
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDescription()).isEqualTo("Нужна дрель");
    }

    @Test
    void findById_shouldReturnRequest() {
        User user = new User(1L, "User", "user@mail.com");
        User otherUser = new User(2L, "Other", "other@mail.com");
        ItemRequest request = new ItemRequest(1L, "Нужна дрель", otherUser, LocalDateTime.now());

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(requestRepository.findById(1L)).thenReturn(Optional.of(request));

        ItemRequestDto result = requestService.findById(1L, 1L);
        assertThat(result).isNotNull();
        assertThat(result.getDescription()).isEqualTo("Нужна дрель");
    }

    @Test
    void findById_shouldThrowNotFoundException_whenRequestNotFound() {
        User user = new User(1L, "User", "user@mail.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(requestRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> requestService.findById(999L, 1L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Запрос с id 999 не найден");
    }

    @Test
    void findByRequestor_shouldThrowNotFoundException_whenUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> requestService.findByRequestor(1L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Пользователь с id 1 не найден");
    }
}