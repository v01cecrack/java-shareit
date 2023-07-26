package ru.practicum.shareit.booking.dao;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.Status;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Integer> {
    List<Booking> findByBooker_IdAndEndIsBefore(Integer bookerId, LocalDateTime end, Sort sort);

    List<Booking> findByBooker_IdAndStartIsAfter(Integer bookerId, LocalDateTime start, Sort sort);

    List<Booking> findByBooker_IdAndStatus(Integer bookerId, Status status, Sort sort);

    List<Booking> findByBooker_IdAndStartIsBeforeAndEndIsAfter(Integer bookerId, LocalDateTime start,
                                                               LocalDateTime ends, Sort sort);

    List<Booking> findByBooker_Id(Integer bookerId);

    List<Booking> findByItemOwnerIdAndEndIsBefore(Integer bookerId, LocalDateTime ends, Sort sort);

    List<Booking> findByItemOwnerIdAndStartIsAfter(Integer bookerId, LocalDateTime starts, Sort sort);

    List<Booking> findByItemOwnerIdAndStartIsBeforeAndEndIsAfter(Integer bookerId, LocalDateTime starts,
                                                                 LocalDateTime ends, Sort sort);

    List<Booking> findByItemOwnerIdAndStatus(Integer bookerId, Status status, Sort sort);

    List<Booking> findByItemOwnerIdOrderByStartDesc(Integer ownerId);

    List<Booking> findBookingByItem_IdAndStatus(Integer itemId, Status status);

    List<Booking> findBookingByStatus(Status status);

    List<Booking> findBookingByItem_Id(Integer itemId);
}
