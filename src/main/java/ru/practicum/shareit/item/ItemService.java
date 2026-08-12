package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingShortDto;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.comment.*;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    public List<ItemResponseWithBookingDto> findByOwner(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));

        List<Item> items = itemRepository.findByOwnerId(userId);
        return items.stream()
                .map(item -> toItemResponseWithBookingDto(item, userId))
                .collect(Collectors.toList());
    }

    public ItemResponseWithBookingDto findById(Long id, Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));

        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Вещь с id " + id + " не найдена"));

        return toItemResponseWithBookingDto(item, userId);
    }

    @Transactional
    public ItemResponseDto create(ItemCreateDto itemCreateDto, Long userId) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));

        Item item = ItemMapper.toItem(itemCreateDto, owner);
        Item createdItem = itemRepository.save(item);
        return ItemMapper.toItemResponseDto(createdItem);
    }

    @Transactional
    public ItemResponseDto update(Long itemId, ItemUpdateDto itemUpdateDto, Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));

        Item existingItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id " + itemId + " не найдена"));

        if (!existingItem.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Пользователь не является владельцем вещи");
        }

        Item updatedItem = ItemMapper.updateItem(existingItem, itemUpdateDto);
        Item result = itemRepository.save(updatedItem);
        return ItemMapper.toItemResponseDto(result);
    }

    public List<ItemResponseDto> search(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }
        return itemRepository.search(text).stream()
                .map(ItemMapper::toItemResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public CommentResponseDto addComment(Long itemId, Long userId, CommentCreateDto commentCreateDto) {
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id " + itemId + " не найдена"));

        boolean hasBooked = bookingRepository.existsByItemIdAndBookerIdAndEndBefore(
                itemId, userId, LocalDateTime.now()
        );

        if (!hasBooked) {
            throw new ValidationException("Пользователь не брал эту вещь в аренду или аренда еще не завершена");
        }

        Comment comment = CommentMapper.toComment(commentCreateDto, item, author);
        Comment savedComment = commentRepository.save(comment);
        return CommentMapper.toCommentResponseDto(savedComment);
    }

    private ItemResponseWithBookingDto toItemResponseWithBookingDto(Item item, Long userId) {
        LocalDateTime now = LocalDateTime.now();

        List<Booking> lastBookings = bookingRepository.findLastBooking(item.getId(), now);
        List<Booking> nextBookings = bookingRepository.findNextBooking(item.getId(), now);

        BookingShortDto lastBooking = lastBookings.isEmpty() ? null :
                BookingMapper.toBookingShortDto(lastBookings.get(0));
        BookingShortDto nextBooking = nextBookings.isEmpty() ? null :
                BookingMapper.toBookingShortDto(nextBookings.get(0));

        List<CommentResponseDto> comments = commentRepository.findByItemId(item.getId()).stream()
                .map(CommentMapper::toCommentResponseDto)
                .collect(Collectors.toList());

        return ItemMapper.toItemResponseWithBookingDto(item, lastBooking, nextBooking, comments);
    }
}