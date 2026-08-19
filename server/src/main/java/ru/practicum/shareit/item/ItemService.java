package ru.practicum.shareit.item;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentMapper;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.ItemResponseWithBookingDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;

    // Явный конструктор вместо @RequiredArgsConstructor
    public ItemService(ItemRepository itemRepository, UserRepository userRepository,
                       BookingRepository bookingRepository, CommentRepository commentRepository,
                       ItemRequestRepository itemRequestRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;
        this.itemRequestRepository = itemRequestRepository;
    }

    @Transactional
    public ItemResponseDto create(ItemCreateDto itemCreateDto, Long userId) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));

        Item item = ItemMapper.toItem(itemCreateDto, owner);

        if (itemCreateDto.getRequestId() != null) {
            ItemRequest request = itemRequestRepository.findById(itemCreateDto.getRequestId())
                    .orElseThrow(() -> new NotFoundException("Запрос с id " + itemCreateDto.getRequestId() + " не найден"));
            item.setRequest(request);
        }

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

    public ItemResponseWithBookingDto findById(Long id, Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));

        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Вещь с id " + id + " не найдена"));

        LocalDateTime now = LocalDateTime.now();

        BookingShortDto lastBooking = null;
        BookingShortDto nextBooking = null;

        if (item.getOwner().getId().equals(userId)) {
            List<Booking> lastBookings = bookingRepository.findLastBooking(item.getId(), now);
            List<Booking> nextBookings = bookingRepository.findNextBooking(item.getId(), now);
            lastBooking = lastBookings.isEmpty() ? null : BookingMapper.toBookingShortDto(lastBookings.get(0));
            nextBooking = nextBookings.isEmpty() ? null : BookingMapper.toBookingShortDto(nextBookings.get(0));
        }

        List<CommentResponseDto> comments = commentRepository.findByItemId(item.getId()).stream()
                .map(CommentMapper::toCommentResponseDto)
                .collect(Collectors.toList());

        return ItemMapper.toItemResponseWithBookingDto(item, lastBooking, nextBooking, comments);
    }

    public List<ItemResponseWithBookingDto> findByOwner(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));

        List<Item> items = itemRepository.findByOwnerId(userId);
        List<Long> itemIds = items.stream().map(Item::getId).collect(Collectors.toList());

        List<Booking> allBookings = bookingRepository.findAllByItemIds(itemIds);
        List<Comment> allComments = commentRepository.findAllByItemIds(itemIds);

        Map<Long, List<Booking>> bookingsByItem = allBookings.stream()
                .collect(Collectors.groupingBy(b -> b.getItem().getId()));
        Map<Long, List<Comment>> commentsByItem = allComments.stream()
                .collect(Collectors.groupingBy(c -> c.getItem().getId()));

        LocalDateTime now = LocalDateTime.now();

        return items.stream()
                .map(item -> {
                    List<Booking> itemBookings = bookingsByItem.getOrDefault(item.getId(), List.of());
                    List<Comment> itemComments = commentsByItem.getOrDefault(item.getId(), List.of());

                    BookingShortDto lastBooking = null;
                    BookingShortDto nextBooking = null;

                    if (item.getOwner().getId().equals(userId)) {
                        List<Booking> lastBookings = itemBookings.stream()
                                .filter(b -> b.getStatus() == BookingStatus.APPROVED && b.getEnd().isBefore(now))
                                .sorted(Comparator.comparing(Booking::getEnd).reversed())
                                .collect(Collectors.toList());
                        List<Booking> nextBookings = itemBookings.stream()
                                .filter(b -> b.getStatus() == BookingStatus.APPROVED && b.getStart().isAfter(now))
                                .sorted(Comparator.comparing(Booking::getStart))
                                .collect(Collectors.toList());

                        lastBooking = lastBookings.isEmpty() ? null : BookingMapper.toBookingShortDto(lastBookings.get(0));
                        nextBooking = nextBookings.isEmpty() ? null : BookingMapper.toBookingShortDto(nextBookings.get(0));
                    }

                    List<CommentResponseDto> commentDtos = itemComments.stream()
                            .map(CommentMapper::toCommentResponseDto)
                            .collect(Collectors.toList());

                    return ItemMapper.toItemResponseWithBookingDto(item, lastBooking, nextBooking, commentDtos);
                })
                .collect(Collectors.toList());
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
}