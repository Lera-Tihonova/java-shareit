package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceTest {

    @Mock
    private ItemRequestRepository requestRepository;

    @Mock
    private UserRepository userRepository;

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
}