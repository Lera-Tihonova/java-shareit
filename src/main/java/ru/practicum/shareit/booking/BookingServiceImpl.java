package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public BookingResponseDto create(BookingCreateDto bookingCreateDto, Long userId) {
        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));

        Item item = itemRepository.findById(bookingCreateDto.getItemId())
                .orElseThrow(() -> new NotFoundException("Вещь с id " + bookingCreateDto.getItemId() + " не найдена"));

        // Проверка: нельзя бронировать свою вещь
        if (item.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Владелец не может бронировать свою вещь");
        }

        // Проверка: вещь должна быть доступна
        if (!item.getAvailable()) {
            throw new ValidationException("Вещь недоступна для бронирования");
        }

        // Проверка дат
        if (bookingCreateDto.getStart().isAfter(bookingCreateDto.getEnd()) ||
                bookingCreateDto.getStart().equals(bookingCreateDto.getEnd())) {
            throw new ValidationException("Дата начала должна быть раньше даты окончания");
        }

        if (bookingCreateDto.getStart().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Дата начала не может быть в прошлом");
        }

        Booking booking = BookingMapper.toBooking(bookingCreateDto, item, booker);
        Booking savedBooking = bookingRepository.save(booking);
        return BookingMapper.toBookingResponseDto(savedBooking);
    }

    @Override
    @Transactional
    public BookingResponseDto approve(Long bookingId, Long userId, Boolean approved) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование с id " + bookingId + " не найдено"));

        // Проверка: только владелец вещи может подтвердить/отклонить
        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new NotFoundException("Только владелец вещи может подтвердить или отклонить бронирование");
        }

        // Проверка: статус должен быть WAITING
        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new ValidationException("Бронирование уже обработано");
        }

        // Проверка: дата начала не должна быть в прошлом
        if (booking.getStart().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Нельзя изменить статус бронирования с прошедшей датой начала");
        }

        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        Booking updatedBooking = bookingRepository.save(booking);
        return BookingMapper.toBookingResponseDto(updatedBooking);
    }

    @Override
    public BookingResponseDto findById(Long bookingId, Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование с id " + bookingId + " не найдено"));

        // Проверка: доступ только для автора бронирования или владельца вещи
        if (!booking.getBooker().getId().equals(userId) &&
                !booking.getItem().getOwner().getId().equals(userId)) {
            throw new NotFoundException("У вас нет прав на просмотр этого бронирования");
        }

        return BookingMapper.toBookingResponseDto(booking);
    }

    @Override
    public List<BookingResponseDto> findAllByUser(Long userId, BookingState state) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));

        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings;

        switch (state) {
            case ALL:
                bookings = bookingRepository.findByBookerId(userId, sort);
                break;
            case CURRENT:
                bookings = bookingRepository.findCurrentByBooker(userId, now, sort);
                break;
            case PAST:
                bookings = bookingRepository.findPastByBooker(userId, now, sort);
                break;
            case FUTURE:
                bookings = bookingRepository.findFutureByBooker(userId, now, sort);
                break;
            case WAITING:
                bookings = bookingRepository.findByBookerIdAndStatus(userId, BookingStatus.WAITING, sort);
                break;
            case REJECTED:
                bookings = bookingRepository.findByBookerIdAndStatus(userId, BookingStatus.REJECTED, sort);
                break;
            default:
                bookings = List.of();
        }

        return bookings.stream()
                .map(BookingMapper::toBookingResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingResponseDto> findAllByOwner(Long userId, BookingState state) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));

        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings;

        switch (state) {
            case ALL:
                bookings = bookingRepository.findByOwnerId(userId, sort);
                break;
            case CURRENT:
                bookings = bookingRepository.findCurrentByOwner(userId, now, sort);
                break;
            case PAST:
                bookings = bookingRepository.findPastByOwner(userId, now, sort);
                break;
            case FUTURE:
                bookings = bookingRepository.findFutureByOwner(userId, now, sort);
                break;
            case WAITING:
                bookings = bookingRepository.findByOwnerIdAndStatus(userId, BookingStatus.WAITING, sort);
                break;
            case REJECTED:
                bookings = bookingRepository.findByOwnerIdAndStatus(userId, BookingStatus.REJECTED, sort);
                break;
            default:
                bookings = List.of();
        }

        return bookings.stream()
                .map(BookingMapper::toBookingResponseDto)
                .collect(Collectors.toList());
    }
}