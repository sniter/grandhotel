package com.grandhotel.booking.repository;

import com.grandhotel.booking.model.Room;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.jdbc.Sql;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class RoomRepositoryTest {

    @Autowired
    private RoomRepository roomRepository;

    @Test
    @Sql("/rooms.sql")
    public void findByNumberNotInList() {
        List<Integer> roomNumberList = Arrays.asList(2,4,6,8,10);
        Page<Room> rooms = roomRepository.findByNumberNotIn(roomNumberList, PageRequest.of(0, 10));

        assertThat(rooms.getTotalElements()).isEqualTo(5);
        assertThat(rooms.get().map(Room::getNumber)).contains(1,3,5,7,9);
    }

    @Test
    @Sql("/rooms.sql")
    public void findByNumberNotInEmptyList() {
        List<Integer> roomNumberList = Collections.emptyList();
        Page<Room> rooms = roomRepository.findByNumberNotIn(roomNumberList, PageRequest.of(0, 10));

        assertThat(rooms.getTotalElements()).isEqualTo(0);
    }


}