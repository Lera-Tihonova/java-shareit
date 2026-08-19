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
    List<Booking> findByBookerId(Long bookerId, Sort sort);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :ownerId")
    List<Booking> findByOwnerId(@Param("ownerId") Long ownerId, Sort sort);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :bookerId AND b.start <= :now AND b.end >= :now")
    List<Booking> findCurrentByBooker(@Param("bookerId") Long bookerId, @Param("now") LocalDateTime now, Sort sort);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :bookerId AND b.start > :now")
    List<Booking> findFutureByBooker(@Param("bookerId") Long bookerId, @Param("now") LocalDateTime now, Sort sort);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :bookerId AND b.end < :now")
    List<Booking> findPastByBooker(@Param("bookerId") Long bookerId, @Param("now") LocalDateTime now, Sort sort);

    List<Booking> findByBookerIdAndStatus(Long bookerId, BookingStatus status, Sort sort);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :ownerId AND b.start <= :now AND b.end >= :now")
    List<Booking> findCurrentByOwner(@Param("ownerId") Long ownerId, @Param("now") LocalDateTime now, Sort sort);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :ownerId AND b.start > :now")
    List<Booking> findFutureByOwner(@Param("ownerId") Long ownerId, @Param("now") LocalDateTime now, Sort sort);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :ownerId AND b.end < :now")
    List<Booking> findPastByOwner(@Param("ownerId") Long ownerId, @Param("now") LocalDateTime now, Sort sort);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :ownerId AND b.status = :status")
    List<Booking> findByOwnerIdAndStatus(@Param("ownerId") Long ownerId, @Param("status") BookingStatus status, Sort sort);

    @Query("SELECT b FROM Booking b WHERE b.item.id IN :itemIds")
    List<Booking> findAllByItemIds(@Param("itemIds") List<Long> itemIds);

    @Query("SELECT b FROM Booking b WHERE b.item.id = :itemId AND b.status = 'APPROVED' AND b.end < :now ORDER BY b.end DESC")
    List<Booking> findLastBooking(@Param("itemId") Long itemId, @Param("now") LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.item.id = :itemId AND b.status = 'APPROVED' AND b.start > :now ORDER BY b.start ASC")
    List<Booking> findNextBooking(@Param("itemId") Long itemId, @Param("now") LocalDateTime now);

    @Query("SELECT COUNT(b) > 0 FROM Booking b WHERE b.item.id = :itemId AND b.booker.id = :bookerId AND b.end < :now AND b.status = 'APPROVED'")
    boolean existsByItemIdAndBookerIdAndEndBefore(@Param("itemId") Long itemId, @Param("bookerId") Long bookerId, @Param("now") LocalDateTime now);

    @Query("SELECT COUNT(b) > 0 FROM Booking b WHERE b.item.id = :itemId AND b.status IN ('APPROVED', 'WAITING') AND b.start < :end AND b.end > :start")
    boolean existsOverlappingBooking(@Param("itemId") Long itemId, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}