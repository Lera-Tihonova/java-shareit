package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.BookingStatus;
import java.time.LocalDateTime;

public class BookingResponseDto {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private BookingItemDto item;
    private BookingUserDto booker;
    private BookingStatus status;

    public BookingResponseDto() {}

    public BookingResponseDto(Long id, LocalDateTime start, LocalDateTime end, BookingItemDto item,
                              BookingUserDto booker, BookingStatus status) {
        this.id = id;
        this.start = start;
        this.end = end;
        this.item = item;
        this.booker = booker;
        this.status = status;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDateTime getStart() { return start; }
    public void setStart(LocalDateTime start) { this.start = start; }

    public LocalDateTime getEnd() { return end; }
    public void setEnd(LocalDateTime end) { this.end = end; }

    public BookingItemDto getItem() { return item; }
    public void setItem(BookingItemDto item) { this.item = item; }

    public BookingUserDto getBooker() { return booker; }
    public void setBooker(BookingUserDto booker) { this.booker = booker; }

    public BookingStatus getStatus() { return status; }
    public void setStatus(BookingStatus status) { this.status = status; }
}