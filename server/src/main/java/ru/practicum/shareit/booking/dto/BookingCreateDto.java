package ru.practicum.shareit.booking.dto;

import java.time.LocalDateTime;

public class BookingCreateDto {
    private Long itemId;
    private LocalDateTime start;
    private LocalDateTime end;

    public BookingCreateDto() {}

    public BookingCreateDto(Long itemId, LocalDateTime start, LocalDateTime end) {
        this.itemId = itemId;
        this.start = start;
        this.end = end;
    }

    public Long getItemId() { return itemId; }
    public void setItemId(Long itemId) { this.itemId = itemId; }

    public LocalDateTime getStart() { return start; }
    public void setStart(LocalDateTime start) { this.start = start; }

    public LocalDateTime getEnd() { return end; }
    public void setEnd(LocalDateTime end) { this.end = end; }
}