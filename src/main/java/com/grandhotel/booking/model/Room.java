package com.grandhotel.booking.model;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;


@Entity
@Table(name="rooms")
@Builder(setterPrefix="with")
@Getter @Setter @ToString
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor @AllArgsConstructor
public class Room implements Serializable {
    @Id
    private Integer number;
    private Integer rooms;
    private Integer floor;

    @OneToMany(mappedBy = "room")
    private Set<Reservation> reservations = new HashSet<>();

    @Version
    private Long version;
}
