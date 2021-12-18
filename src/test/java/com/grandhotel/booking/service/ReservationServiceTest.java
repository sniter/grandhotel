package com.grandhotel.booking.service;

import com.grandhotel.booking.model.Reservation;
import com.grandhotel.booking.model.Room;
import com.grandhotel.booking.repository.ReservationRepository;
import com.grandhotel.booking.repository.RoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {
    @Mock
    private ReservationRepository reservationRepository;
    @Mock
    private RoomRepository roomRepository;

    private ReservationService reservationService;

    private Reservation reservationRequest, approvedReservation;
    private Room room;
    private Calendar startDate, endDate;
    private Pageable pageable;

    @BeforeEach
    void setUp(){
        reservationService = new ReservationService(reservationRepository, roomRepository);
        room = Room.builder().withNumber(1).withRooms(1).withFloor(1).build();
        startDate = new GregorianCalendar(2021, Calendar.DECEMBER, 1);
        endDate = new GregorianCalendar(2021, Calendar.DECEMBER, 31);
        reservationRequest = Reservation.builder()
                .withStart(startDate)
                .withEnd(endDate)
                .withRoom(room)
                .build();

        approvedReservation = Reservation.builder()
                .withId(1L)
                .withStart(startDate)
                .withEnd(endDate)
                .withRoom(room)
                .build();

        pageable = PageRequest.of(0, 10);
    }

    @Test
    public void bookRoom(){
        when(roomRepository.getById(1)).thenReturn(room);
        when(reservationRepository.isRoomVacantForPeriod(1, startDate, endDate)).thenReturn(true);
        when(reservationRepository.save(reservationRequest)).thenReturn(approvedReservation);

        Optional<Reservation> actualReservation = reservationService.book(reservationRequest);
        assertThat(actualReservation.isPresent()).isTrue();
    }

    @Test
    public void bookRoomFails(){
        when(reservationRepository.isRoomVacantForPeriod(
                reservationRequest.getRoom().getNumber(),
                startDate,
                endDate)).thenReturn(false);

        Optional<Reservation> actualReservation = reservationService.book(reservationRequest);
        assertThat(actualReservation.isEmpty()).isTrue();
    }

    @Test
    public void checkRoomReservations(){
        when(reservationRepository.findRoomReservationsPaged(startDate, endDate, room.getNumber(), pageable))
                .thenReturn(new PageImpl<>(Collections.singletonList(approvedReservation)));

        Page<Reservation> reservations = reservationService.reservations(startDate, endDate, room.getNumber(), pageable);
        assertThat(reservations.getTotalElements()).isEqualTo(1);
    }

    @Test
    public void checkNoRoomReservations(){
        when(reservationRepository.findRoomReservationsPaged(startDate, endDate, room.getNumber(), pageable))
                .thenReturn(new PageImpl<>(Collections.emptyList()));

        Page<Reservation> reservations = reservationService.reservations(startDate, endDate, room.getNumber(), pageable);
        assertThat(reservations.getTotalElements()).isEqualTo(0);
    }

    @Test
    public void checkHotelReservations(){
        when(reservationRepository.findHotelReservationsPaged(startDate, endDate, pageable))
                .thenReturn(new PageImpl<>(Collections.singletonList(approvedReservation)));

        Page<Reservation> reservations = reservationService.reservations(startDate, endDate, null, pageable);
        assertThat(reservations.getTotalElements()).isEqualTo(1);
    }

    @Test
    public void checkNoHotelReservations(){
        when(reservationRepository.findHotelReservationsPaged(startDate, endDate, pageable))
                .thenReturn(new PageImpl<>(Collections.emptyList()));

        Page<Reservation> reservations = reservationService.reservations(startDate, endDate, null, pageable);
        assertThat(reservations.getTotalElements()).isEqualTo(0);
    }

    @Test
    public void checkAvailability(){
        List<Integer> excludedRooms = Collections.singletonList(2);
        List<Room> rooms = Collections.singletonList(room);

        when(reservationRepository.findOccupiedRoomsByPeriod(startDate, endDate)).thenReturn(excludedRooms);
        when(roomRepository.findByNumberNotIn(excludedRooms, pageable)).thenReturn(new PageImpl<>(rooms));

        Page<Reservation> reservations = reservationService.vacancies(startDate, endDate, pageable);
        assertThat(reservations.getTotalElements()).isEqualTo(1);
        assertThat(reservations.getContent().get(0).getRoom().getNumber()).isEqualTo(1);
    }

    @Test
    public void checkNoAvailability(){
        List<Integer> excludedRooms = Collections.singletonList(1);
        List<Room> rooms = Collections.emptyList();

        when(reservationRepository.findOccupiedRoomsByPeriod(startDate, endDate)).thenReturn(excludedRooms);
        when(roomRepository.findByNumberNotIn(excludedRooms, pageable)).thenReturn(new PageImpl<>(rooms));

        Page<Reservation> reservations = reservationService.vacancies(startDate, endDate, pageable);
        assertThat(reservations.getTotalElements()).isEqualTo(0);
    }

    @Test
    public void checkAllRoomsAvailabile(){
        List<Integer> excludedRooms = Collections.emptyList();
        List<Room> rooms = Collections.singletonList(room);

        when(reservationRepository.findOccupiedRoomsByPeriod(startDate, endDate)).thenReturn(excludedRooms);
        when(roomRepository.findAll(pageable)).thenReturn(new PageImpl<>(rooms));

        Page<Reservation> reservations = reservationService.vacancies(startDate, endDate, pageable);
        assertThat(reservations.getTotalElements()).isEqualTo(1);
    }
}