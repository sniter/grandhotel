package com.grandhotel.booking.repository;

import com.grandhotel.booking.model.Reservation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ReservationRepositoryTest {
    @Autowired
    ReservationRepository reservationRepository;

    private Calendar start, end;

    @BeforeEach
    void setUp() {
        start = new GregorianCalendar(2021, Calendar.DECEMBER, 1);
        end = new GregorianCalendar(2021, Calendar.DECEMBER, 31);
    }

    @Test
    @SqlGroup({
            @Sql("/rooms.sql"),
            @Sql("/reservations.sql"),
    })
    public void checkOccupiedRooms() {
        List<Integer> rooms = reservationRepository.findOccupiedRoomsByPeriod(start, end);
        assertThat(rooms.size()).isEqualTo(5);
        assertThat(rooms).contains(2,3,4,5,7);
    }

    @Test
    @SqlGroup({
            @Sql("/rooms.sql"),
            @Sql("/reservations.sql"),
    })
    public void checkReservations(){
        Pageable pageable = Pageable.ofSize(10).withPage(0);
        Page<Reservation> reservationPage = reservationRepository.findHotelReservationsPaged(start, end, pageable);
        assertThat(reservationPage.getTotalElements()).isEqualTo(5);
        assertThat(reservationPage.get().map(r -> r.getRoom().getNumber())).contains(2,3,4,5,7);
    }

    @Test
    @SqlGroup({
            @Sql("/rooms.sql"),
            @Sql("/reservations.sql"),
    })
    public void checkIfCanBookRoom(){
        boolean canBook = reservationRepository.isRoomVacantForPeriod(1, start, end);
        assertThat(canBook).isTrue();
    }

    @Test
    @SqlGroup({
            @Sql("/rooms.sql"),
            @Sql("/reservations.sql"),
    })
    public void checkIfCanNotBookRoom(){
        boolean canBook = reservationRepository.isRoomVacantForPeriod(2, start, end);
        assertThat(canBook).isFalse();
    }
}