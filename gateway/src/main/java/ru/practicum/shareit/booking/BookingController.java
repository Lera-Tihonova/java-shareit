package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {

    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestBody BookItemRequestDto requestDto,
                                         @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("POST /bookings, userId={}", userId);
        return bookingClient.bookItem(userId, requestDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approve(@PathVariable Long bookingId,
                                          @RequestParam Boolean approved,
                                          @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("PATCH /bookings/{}, approved={}, userId={}", bookingId, approved, userId);
        return bookingClient.patchBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> findById(@PathVariable Long bookingId,
                                           @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("GET /bookings/{}, userId={}", bookingId, userId);
        return bookingClient.getBooking(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> findAllByUser(@RequestParam(defaultValue = "ALL") BookingState state,
                                                @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("GET /bookings, state={}, userId={}", state, userId);
        return bookingClient.getBookings(userId, state, 0, 20);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> findAllByOwner(@RequestParam(defaultValue = "ALL") BookingState state,
                                                 @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("GET /bookings/owner, state={}, userId={}", state, userId);
        return bookingClient.getBookingsByOwner(userId, state, 0, 20);
    }
}