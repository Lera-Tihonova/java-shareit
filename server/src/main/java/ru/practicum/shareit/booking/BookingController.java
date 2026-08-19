package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

import java.util.List;

@RestController
@RequestMapping("/bookings")
public class BookingController {
    private final BookingService bookingService;

    // Явный конструктор
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public BookingResponseDto create(
            @Valid @RequestBody BookingCreateDto bookingCreateDto,
            @RequestHeader("X-Sharer-User-Id") Long userId
    ) {
        return bookingService.create(bookingCreateDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingResponseDto approve(
            @PathVariable Long bookingId,
            @RequestParam Boolean approved,
            @RequestHeader("X-Sharer-User-Id") Long userId
    ) {
        return bookingService.approve(bookingId, userId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingResponseDto findById(
            @PathVariable Long bookingId,
            @RequestHeader("X-Sharer-User-Id") Long userId
    ) {
        return bookingService.findById(bookingId, userId);
    }

    @GetMapping
    public List<BookingResponseDto> findAllByUser(
            @RequestParam(defaultValue = "ALL") BookingState state,
            @RequestHeader("X-Sharer-User-Id") Long userId
    ) {
        return bookingService.findAllByUser(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingResponseDto> findAllByOwner(
            @RequestParam(defaultValue = "ALL") BookingState state,
            @RequestHeader("X-Sharer-User-Id") Long userId
    ) {
        return bookingService.findAllByOwner(userId, state);
    }
}