package com.grandhotel.booking.model;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Calendar;

@Entity
@Table(name="reservations")
@Builder(setterPrefix="with")
@Getter @Setter @ToString
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor @AllArgsConstructor
public class Reservation implements Serializable {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Temporal(TemporalType.DATE)
    @Column(name = "start_date")
    private Calendar start;

    @Temporal(TemporalType.DATE)
    @Column(name = "end_date")
    private Calendar end;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room")
    private Room room;

    @Version
    private Long version;
}
