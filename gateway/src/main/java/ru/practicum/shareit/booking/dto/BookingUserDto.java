package ru.practicum.shareit.booking.dto;

public class BookingUserDto {
    private Long id;
    private String name;

    public BookingUserDto() {
    }

    public BookingUserDto(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}