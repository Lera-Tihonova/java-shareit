package ru.practicum.shareit.booking;

import java.util.List;

public interface BookingService {
    BookingResponseDto create(BookingCreateDto bookingCreateDto, Long userId);

    BookingResponseDto approve(Long bookingId, Long userId, Boolean approved);

    BookingResponseDto findById(Long bookingId, Long userId);

    List<BookingResponseDto> findAllByUser(Long userId, BookingState state);

    List<BookingResponseDto> findAllByOwner(Long userId, BookingState state);
}