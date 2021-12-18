package com.grandhotel.booking.repository;

import com.grandhotel.booking.model.Reservation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Calendar;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @Query(value = "SELECT count(r.id) = 0 " +
            "FROM reservations r " +
            "WHERE " +
            "room = :room AND (" +
            "(r.start_date BETWEEN :start AND :stop) OR " +
            "(r.end_date BETWEEN :start AND :stop) OR " +
            "(r.start_date <= :start AND r.end_date >= :stop))", nativeQuery = true)
    Boolean isRoomVacantForPeriod(
            @Param("room") Integer roomNumber,
            @Param("start") Calendar start,
            @Param("stop") Calendar stop
    );

    @Query(value = "SELECT DISTINCT " +
            "r.room " +
            "FROM reservations r " +
            "WHERE " +
            "(r.start_date BETWEEN :start AND :stop) OR " +
            "(r.end_date BETWEEN :start AND :stop) OR " +
            "(r.start_date <= :start AND r.end_date >= :stop)", nativeQuery = true)
    List<Integer> findOccupiedRoomsByPeriod(
            @Param("start") Calendar start,
            @Param("stop") Calendar stop
    );

    @Query(value = "SELECT r FROM Reservation r WHERE " +
            "(r.start BETWEEN :start AND :stop) OR " +
            "(r.end BETWEEN :start AND :stop) OR " +
            "(r.start <= :start AND r.end >= :stop)")
    Page<Reservation> findHotelReservationsPaged(
            @Param("start") Calendar start,
            @Param("stop") Calendar stop,
            Pageable pageable
    );

    @Query(value = "SELECT r FROM reservations r WHERE " +
            "r.room = :room" +
            "(r.start_date BETWEEN :start AND :stop) OR " +
            "(r.end_date BETWEEN :start AND :stop) OR " +
            "(r.start_date <= :start AND r.end_date >= :stop)", nativeQuery = true)
    Page<Reservation> findRoomReservationsPaged(
            @Param("start") Calendar start,
            @Param("stop") Calendar stop,
            @Param("room") Integer roomNumber,
            Pageable pageable
    );
}
