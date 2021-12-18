package com.grandhotel.booking.controller;

import com.grandhotel.booking.model.Reservation;
import com.grandhotel.booking.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletResponse;
import java.util.Calendar;
import java.util.Optional;

@RestController
@RequestMapping("/reservation")
@RequiredArgsConstructor
public class ReservationController {
    private final ReservationService reservationService;
    private final Integer pageSize;


    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public Reservation book(@RequestBody final Reservation reservation, final HttpServletResponse response){
        Optional<Reservation> mayBeReservation = reservationService.book(reservation);
        if(mayBeReservation.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Reservation is unavailable");
        response.addHeader(HttpHeaders.LOCATION, String.format("/reservation/%d", mayBeReservation.get().getId()));
        return mayBeReservation.get();

    }

    @GetMapping("/availability")
    public Page<Reservation> availability(
            @RequestParam("start") @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) Calendar startDate,
            @RequestParam("end") @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) Calendar endDate,
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page
            ){
        if(page<1)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Parameter page should be greater than 0");
        return reservationService.vacancies(startDate, endDate, PageRequest.of(page - 1, pageSize));
    }

    @GetMapping
    public Page<Reservation> reservationList(
            @RequestParam("start") @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) Calendar startDate,
            @RequestParam("end") @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) Calendar endDate,
            @RequestParam(value = "room", required = false) Integer roomNumber,
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page
    ){
        if(page < 1)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Parameter page should be greater than 0");
        if(roomNumber != null && roomNumber < 1)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Parameter room should be greater than 0");
        return reservationService.reservations(startDate, endDate, roomNumber, PageRequest.of(page - 1, pageSize));
    }

}
