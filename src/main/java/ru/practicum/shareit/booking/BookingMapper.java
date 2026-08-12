package ru.practicum.shareit.booking;

import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

public class BookingMapper {

    public static Booking toBooking(BookingCreateDto bookingCreateDto, Item item, User booker) {
        if (bookingCreateDto == null) {
            return null;
        }
        Booking booking = new Booking();
        booking.setStart(bookingCreateDto.getStart());
        booking.setEnd(bookingCreateDto.getEnd());
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);
        return booking;
    }

    public static BookingResponseDto toBookingResponseDto(Booking booking) {
        if (booking == null) {
            return null;
        }
        return new BookingResponseDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getItem(),
                booking.getBooker(),
                booking.getStatus()
        );
    }

    public static BookingShortDto toBookingShortDto(Booking booking) {
        if (booking == null) {
            return null;
        }
        return new BookingShortDto(
                booking.getId(),
                booking.getBooker().getId(),
                booking.getStart(),
                booking.getEnd()
        );
    }
}