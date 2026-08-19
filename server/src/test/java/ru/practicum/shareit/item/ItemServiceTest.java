package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.request.ItemRequestRepository;
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
class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @InjectMocks
    private ItemService itemService;

    private User owner;
    private Item item;

    @BeforeEach
    void setUp() {
        owner = new User(1L, "Owner", "owner@mail.com");
        item = new Item(1L, "Дрель", "Мощная дрель", true, owner, null);
    }

    @Test
    void create_shouldCreateItem() {
        ItemCreateDto dto = new ItemCreateDto("Дрель", "Мощная дрель", true, null);
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(itemRepository.save(any(Item.class))).thenReturn(item);
        ItemResponseDto result = itemService.create(dto, 1L);
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Дрель");
    }

    @Test
    void create_shouldThrowNotFoundException_whenUserNotFound() {
        ItemCreateDto dto = new ItemCreateDto("Дрель", "Мощная дрель", true, null);
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> itemService.create(dto, 1L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Пользователь с id 1 не найден");
    }

    @Test
    void update_shouldUpdateItem() {
        User newUser = new User(1L, "Owner", "owner@mail.com");
        Item existing = new Item(1L, "Старая дрель", "Описание", true, newUser, null);
        when(userRepository.findById(1L)).thenReturn(Optional.of(newUser));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(itemRepository.save(any(Item.class))).thenReturn(existing);

        ItemUpdateDto updateDto = new ItemUpdateDto("Новая дрель", null, null);
        ItemResponseDto result = itemService.update(1L, updateDto, 1L);
        assertThat(result.getName()).isEqualTo("Новая дрель");
    }

    @Test
    void findById_shouldReturnItem() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        ItemResponseWithBookingDto result = itemService.findById(1L, 1L);
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Дрель");
    }

    @Test
    void addComment_shouldAddComment() {
        User author = new User(2L, "Author", "author@mail.com");
        CommentCreateDto commentDto = new CommentCreateDto("Отличная вещь!");
        when(userRepository.findById(2L)).thenReturn(Optional.of(author));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(bookingRepository.existsByItemIdAndBookerIdAndEndBefore(anyLong(), anyLong(), any(LocalDateTime.class)))
                .thenReturn(true);
        when(commentRepository.save(any(Comment.class))).thenAnswer(invocation -> {
            Comment c = invocation.getArgument(0);
            c.setId(1L);
            return c;
        });

        CommentResponseDto result = itemService.addComment(1L, 2L, commentDto);
        assertThat(result).isNotNull();
        assertThat(result.getText()).isEqualTo("Отличная вещь!");
    }

    @Test
    void addComment_shouldThrowValidationException_whenUserNotBooked() {
        User author = new User(2L, "Author", "author@mail.com");
        CommentCreateDto commentDto = new CommentCreateDto("Отличная вещь!");
        when(userRepository.findById(2L)).thenReturn(Optional.of(author));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(bookingRepository.existsByItemIdAndBookerIdAndEndBefore(anyLong(), anyLong(), any(LocalDateTime.class)))
                .thenReturn(false);
        assertThatThrownBy(() -> itemService.addComment(1L, 2L, commentDto))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Пользователь не брал эту вещь в аренду или аренда еще не завершена");
    }

    @Test
    void search_shouldReturnItems() {
        when(itemRepository.search("дрель")).thenReturn(List.of(item));
        List<ItemResponseDto> result = itemService.search("дрель");
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Дрель");
    }

    @Test
    void search_shouldReturnEmptyList_whenTextIsBlank() {
        List<ItemResponseDto> result = itemService.search("");
        assertThat(result).isEmpty();
        verify(itemRepository, never()).search(anyString());
    }
}