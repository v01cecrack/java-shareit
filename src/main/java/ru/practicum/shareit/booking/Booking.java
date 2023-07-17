package ru.practicum.shareit.booking;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Data
@Entity
@Table(name = "bookings")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "item_id")
    private int itemId;
    @Column(name = "booker_id")
    private int bookerId;
    @Column(name = "start_date")
    private LocalDateTime start;
    @Column(name = "end_time")
    private LocalDateTime end;
    private String status; //TODO ENUM


}
