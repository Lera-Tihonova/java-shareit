package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private User booker;
    private User owner;
    private Item item;
    private BookingCreateDto createDto;

    @BeforeEach
    void setUp() {
        booker = new User(1L, "Booker", "booker@mail.com");
        owner = new User(2L, "Owner", "owner@mail.com");
        item = new Item(1L, "Дрель", "Мощная дрель", true, owner, null);
        createDto = new BookingCreateDto();
        createDto.setItemId(1L);
        createDto.setStart(LocalDateTime.now().plusDays(1));
        createDto.setEnd(LocalDateTime.now().plusDays(2));
    }

    @Test
    void create_shouldCreateBooking() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(bookingRepository.existsOverlappingBooking(anyLong(), any(), any())).thenReturn(false);
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> {
            Booking b = invocation.getArgument(0);
            b.setId(1L);
            return b;
        });

        BookingResponseDto result = bookingService.create(createDto, 1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    void create_shouldThrowNotFoundException_whenUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.create(createDto, 1L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Пользователь с id 1 не найден");
    }

    @Test
    void create_shouldThrowNotFoundException_whenItemNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.create(createDto, 1L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Вещь с id 1 не найдена");
    }

    @Test
    void create_shouldThrowValidationException_whenItemNotAvailable() {
        item.setAvailable(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        assertThatThrownBy(() -> bookingService.create(createDto, 1L))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Вещь недоступна для бронирования");
    }

    @Test
    void create_shouldThrowValidationException_whenOwnerBookOwnItem() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(owner));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        assertThatThrownBy(() -> bookingService.create(createDto, 2L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Владелец не может бронировать свою вещь");
    }

    @Test
    void create_shouldThrowValidationException_whenStartInPast() {
        createDto.setStart(LocalDateTime.now().minusDays(1));
        when(userRepository.findById(1L)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        assertThatThrownBy(() -> bookingService.create(createDto, 1L))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Дата начала не может быть в прошлом");
    }

    @Test
    void approve_shouldApproveBooking() {
        Booking booking = new Booking(1L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2),
                item, booker, BookingStatus.WAITING);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingResponseDto result = bookingService.approve(1L, 2L, true);

        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(BookingStatus.APPROVED);
    }

    @Test
    void approve_shouldThrowForbiddenException_whenNotOwner() {
        Booking booking = new Booking(1L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2),
                item, booker, BookingStatus.WAITING);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        assertThatThrownBy(() -> bookingService.approve(1L, 3L, true))
                .isInstanceOf(ForbiddenException.class)
                .hasMessageContaining("Только владелец вещи может подтвердить или отклонить бронирование");
    }

    @Test
    void approve_shouldThrowValidationException_whenAlreadyProcessed() {
        Booking booking = new Booking(1L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2),
                item, booker, BookingStatus.APPROVED);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        assertThatThrownBy(() -> bookingService.approve(1L, 2L, true))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Бронирование уже обработано");
    }

    @Test
    void findById_shouldReturnBooking() {
        Booking booking = new Booking(1L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2),
                item, booker, BookingStatus.WAITING);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        BookingResponseDto result = bookingService.findById(1L, 1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void findById_shouldThrowForbiddenException_whenUserNotBookerOrOwner() {
        Booking booking = new Booking(1L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2),
                item, booker, BookingStatus.WAITING);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        assertThatThrownBy(() -> bookingService.findById(1L, 3L))
                .isInstanceOf(ForbiddenException.class)
                .hasMessageContaining("У вас нет прав на просмотр этого бронирования");
    }
}