package ru.practicum.shareit.booking.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.Status;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Integer> {
    Page<Booking> findByBooker_IdAndEndIsBeforeOrderByStartDesc(Integer bookerId, LocalDateTime ends, Pageable pageable);

    Page<Booking> findByBooker_IdAndStartIsAfterOrderByStartDesc(Integer bookerId, LocalDateTime starts, Pageable pageable);

    Page<Booking> findByBooker_IdAndStatusOrderByStartDesc(Integer bookerId, Status status, Pageable pageable);

    Page<Booking> findByBooker_IdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(Integer bookerId, LocalDateTime starts,
                                                                               LocalDateTime ends, Pageable pageable);

    List<Booking> findBookingByItem_IdAndStatus(Integer itemId, Status status);

    List<Booking> findBookingByStatus(Status status);

    List<Booking> findBookingByItem_Id(Integer itemId);

    Page<Booking> findByItemOwnerIdAndEndIsBeforeOrderByStartDesc(Integer bookerId, LocalDateTime ends, Pageable pageable);

    Page<Booking> findByItemOwnerIdAndStartIsAfterOrderByStartDesc(Integer bookerId, LocalDateTime starts, Pageable pageable);

    Page<Booking> findByItemOwnerIdAndStatusOrderByStartDesc(Integer bookerId, Status status, Pageable pageable);

    Page<Booking> findByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(Integer bookerId, LocalDateTime starts,
                                                                                 LocalDateTime ends, Pageable pageable);

    Page<Booking> findBookingsByBooker_IdOrderByStartDesc(Integer bookerId, Pageable pageable);

    Page<Booking> findByItemOwnerIdOrderByStartDesc(Integer ownerId, Pageable pageable);
}
