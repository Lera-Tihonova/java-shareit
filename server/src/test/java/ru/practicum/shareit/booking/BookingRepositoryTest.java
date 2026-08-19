package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class BookingRepositoryTest {

    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;

    @Test
    void findByBookerId_shouldReturnBookings() {
        User booker = userRepository.save(new User(null, "Booker", "booker@mail.com"));
        User owner = userRepository.save(new User(null, "Owner", "owner@mail.com"));
        Item item = itemRepository.save(new Item(null, "Дрель", "Мощная дрель", true, owner, null));
        Booking booking = bookingRepository.save(new Booking(null, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2), item, booker, BookingStatus.WAITING));

        List<Booking> bookings = bookingRepository.findByBookerId(booker.getId(), Sort.by(Sort.Direction.DESC, "start"));
        assertThat(bookings).isNotEmpty();
        assertThat(bookings.get(0).getId()).isEqualTo(booking.getId());
    }

    @Test
    void existsOverlappingBooking_shouldReturnTrue_whenOverlapExists() {
        User booker1 = userRepository.save(new User(null, "Booker1", "booker1@mail.com"));
        User owner = userRepository.save(new User(null, "Owner", "owner@mail.com"));
        Item item = itemRepository.save(new Item(null, "Дрель", "Мощная дрель", true, owner, null));
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        bookingRepository.save(new Booking(null, start, end, item, booker1, BookingStatus.APPROVED));

        boolean exists = bookingRepository.existsOverlappingBooking(item.getId(), start.plusHours(1), end.minusHours(1));
        assertThat(exists).isTrue();
    }

    @Test
    void existsOverlappingBooking_shouldReturnFalse_whenNoOverlap() {
        User booker1 = userRepository.save(new User(null, "Booker1", "booker1@mail.com"));
        User owner = userRepository.save(new User(null, "Owner", "owner@mail.com"));
        Item item = itemRepository.save(new Item(null, "Дрель", "Мощная дрель", true, owner, null));
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        bookingRepository.save(new Booking(null, start, end, item, booker1, BookingStatus.APPROVED));

        boolean exists = bookingRepository.existsOverlappingBooking(item.getId(), start.plusDays(2), end.plusDays(3));
        assertThat(exists).isFalse();
    }
}