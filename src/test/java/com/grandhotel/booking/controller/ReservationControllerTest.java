package com.grandhotel.booking.controller;

import com.grandhotel.booking.model.Reservation;
import com.grandhotel.booking.model.Room;
import com.grandhotel.booking.service.ReservationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReservationController.class)
@Import(ControllersConfig.class)
class ReservationControllerTest {

    @MockBean
    private ReservationService reservationService;

    @Autowired
    private MockMvc mockMvc;

    private String urlPrefix, reservationJsonRepr;
    private Calendar startDate, endDate;
    private Reservation reservation, confirmedReservation;
    private Page<Reservation> reservations;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        urlPrefix = "/reservation";
        reservationJsonRepr = "{" +
                "\"room\":{\"number\": 1, \"rooms\": 1, \"floor\": 1}," +
                "\"start\": \"2021-12-01\", " +
                "\"end\": \"2021-12-31\"" +
                "}";

        startDate = new GregorianCalendar(2021, Calendar.DECEMBER, 1);
        endDate = new GregorianCalendar(2021, Calendar.DECEMBER, 31);

        Room room1 = Room.builder()
                .withNumber(1)
                .withRooms(1)
                .withFloor(1)
                .build();

        reservation = Reservation.builder()
                .withRoom(room1)
                .withStart(startDate)
                .withEnd(endDate)
                .build();

        confirmedReservation = Reservation.builder()
                .withId(1L)
                .withStart(startDate)
                .withEnd(endDate)
                .withRoom(room1)
                .build();

        reservations = new PageImpl<>(
                Collections.singletonList(reservation),
                Pageable.ofSize(1),
                0
        );

        pageable = PageRequest.of(0,10);
    }

    @Test
    public void bookReservation() throws Exception {
        when(reservationService.book(any(Reservation.class)))
                .thenReturn(Optional.of(confirmedReservation));

        mockMvc.perform(post(urlPrefix)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(reservationJsonRepr))
                .andExpect(status().isCreated())
                .andExpect(header().string(HttpHeaders.LOCATION, urlPrefix + "/1"));
    }

    @Test
    public void bookInvalidReservation() throws Exception {
        when(reservationService.book(any(Reservation.class)))
                .thenReturn(Optional.empty());

        mockMvc.perform(post(urlPrefix)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(reservationJsonRepr))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void checkAvailability() throws Exception {
        when(reservationService.vacancies(startDate, endDate, pageable))
                .thenReturn(reservations);

        mockMvc.perform(get(urlPrefix + "/availability" + String.format("?start=%s&end=%s", "2021-12-01", "2021-12-31"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void checkAvailabilityWithInvalidUrls() throws Exception {
        List<String> invalidUrls = Arrays.asList(
                String.format("?start=%s&end=%s", "2021-13-01", "2021-12-31"),
                String.format("?start=%s&end=%s", "2021-12-01", "2021-13-31"),
                String.format("?start=%s&end=%s&page=%s", "2021-12-01", "2021-12-31", "-1"),
                String.format("?start=%s", "2021-12-01"),
                String.format("?end=%s", "2021-12-01")
        );

        for (String urlParams : invalidUrls) {
            mockMvc.perform(get(urlPrefix + "/availability" + urlParams)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());
        }

    }

    @Test
    public void checkReservations() throws Exception {
        Pageable pageablee = Pageable.ofSize(10).withPage(0);
        when(reservationService.reservations(startDate, endDate, 1, pageablee))
                .thenReturn(reservations);

        mockMvc.perform(get(urlPrefix + String.format("?start=%s&end=%s", "2021-12-01", "2021-12-31"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void checkReservationsWithInvalidUrls() throws Exception {
        List<String> invalidUrls = Arrays.asList(
                String.format("?start=%s&end=%s", "2021-13-01", "2021-12-31"),
                String.format("?start=%s&end=%s", "2021-12-01", "2021-13-31"),
                String.format("?start=%s&end=%s&page=%s", "2021-12-01", "2021-12-31", "-1"),
                String.format("?start=%s&end=%s&room=%s", "2021-12-01", "2021-12-31", "-1"),
                String.format("?start=%s", "2021-12-01"),
                String.format("?end=%s", "2021-12-01")
        );

        for (String urlParams : invalidUrls) {
            mockMvc.perform(get(urlPrefix + urlParams)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());
        }
    }

}