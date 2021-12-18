package com.grandhotel.booking.service;

import com.grandhotel.booking.model.Reservation;
import com.grandhotel.booking.model.Room;
import com.grandhotel.booking.repository.ReservationRepository;
import com.grandhotel.booking.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final RoomRepository roomRepository;

    public Optional<Reservation> book(final Reservation reservation) {
        boolean isVacant = reservationRepository.isRoomVacantForPeriod(
                reservation.getRoom().getNumber(),
                reservation.getStart(),
                reservation.getEnd()
        );

        if (!isVacant)
            return Optional.empty();

        Room room = roomRepository.getById(reservation.getRoom().getNumber());
        reservation.setRoom(room);

        return Optional.of(reservationRepository.save(reservation));

    }

    public Page<Reservation> vacancies(Calendar startDate, Calendar endDate, Pageable pageable) {
        Page<Room> vacantRooms;
        List<Integer> occupiedRooms = reservationRepository.findOccupiedRoomsByPeriod(startDate, endDate);

        if(occupiedRooms.isEmpty())
            vacantRooms = roomRepository.findAll(pageable);
        else
            vacantRooms = roomRepository.findByNumberNotIn(occupiedRooms, pageable);


        return new PageImpl<>(
                vacantRooms.get().map(r -> Reservation.builder().withRoom(r).withStart(startDate).withEnd(endDate).build()).collect(Collectors.toList()),
                vacantRooms.getPageable(),
                vacantRooms.getTotalElements()
        );
    }

    public Page<Reservation> reservations(Calendar startDate, Calendar endDate, Integer roomNumber, Pageable pageable) {
        if(roomNumber == null)
            return reservationRepository.findHotelReservationsPaged(startDate, endDate, pageable);
        else
            return reservationRepository.findRoomReservationsPaged(startDate, endDate, roomNumber, pageable);
    }
}
