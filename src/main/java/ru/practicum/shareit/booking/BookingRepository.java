package ru.practicum.shareit.booking;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    // Поиск бронирований по bookerId
    List<Booking> findByBookerId(Long bookerId, Sort sort);

    // Поиск бронирований по ownerId (владелец вещи)
    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :ownerId")
    List<Booking> findByOwnerId(@Param("ownerId") Long ownerId, Sort sort);

    // Поиск текущих бронирований
    @Query("SELECT b FROM Booking b WHERE b.booker.id = :bookerId AND b.start <= :now AND b.end >= :now")
    List<Booking> findCurrentByBooker(@Param("bookerId") Long bookerId, @Param("now") LocalDateTime now, Sort sort);

    // Поиск будущих бронирований
    @Query("SELECT b FROM Booking b WHERE b.booker.id = :bookerId AND b.start > :now")
    List<Booking> findFutureByBooker(@Param("bookerId") Long bookerId, @Param("now") LocalDateTime now, Sort sort);

    // Поиск прошлых бронирований
    @Query("SELECT b FROM Booking b WHERE b.booker.id = :bookerId AND b.end < :now")
    List<Booking> findPastByBooker(@Param("bookerId") Long bookerId, @Param("now") LocalDateTime now, Sort sort);

    // Поиск бронирований по статусу
    List<Booking> findByBookerIdAndStatus(Long bookerId, BookingStatus status, Sort sort);

    // Поиск текущих бронирований владельца
    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :ownerId AND b.start <= :now AND b.end >= :now")
    List<Booking> findCurrentByOwner(@Param("ownerId") Long ownerId, @Param("now") LocalDateTime now, Sort sort);

    // Поиск будущих бронирований владельца
    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :ownerId AND b.start > :now")
    List<Booking> findFutureByOwner(@Param("ownerId") Long ownerId, @Param("now") LocalDateTime now, Sort sort);

    // Поиск прошлых бронирований владельца
    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :ownerId AND b.end < :now")
    List<Booking> findPastByOwner(@Param("ownerId") Long ownerId, @Param("now") LocalDateTime now, Sort sort);

    // Поиск бронирований владельца по статусу
    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :ownerId AND b.status = :status")
    List<Booking> findByOwnerIdAndStatus(@Param("ownerId") Long ownerId, @Param("status") BookingStatus status, Sort sort);

    // Поиск бронирований по вещи
    List<Booking> findByItemId(Long itemId);

    // Поиск последнего завершенного бронирования
    @Query("SELECT b FROM Booking b WHERE b.item.id = :itemId AND b.status = 'APPROVED' AND b.end < :now ORDER BY b.end DESC")
    List<Booking> findLastBooking(@Param("itemId") Long itemId, @Param("now") LocalDateTime now);

    // Поиск следующего бронирования
    @Query("SELECT b FROM Booking b WHERE b.item.id = :itemId AND b.status = 'APPROVED' AND b.start > :now ORDER BY b.start ASC")
    List<Booking> findNextBooking(@Param("itemId") Long itemId, @Param("now") LocalDateTime now);

    // Проверка существования завершенного бронирования
    @Query("SELECT COUNT(b) > 0 FROM Booking b WHERE b.item.id = :itemId AND b.booker.id = :bookerId AND b.end < :now AND b.status = 'APPROVED'")
    boolean existsByItemIdAndBookerIdAndEndBefore(@Param("itemId") Long itemId, @Param("bookerId") Long bookerId, @Param("now") LocalDateTime now);
}