package com.grandhotel.booking.service;

import com.grandhotel.booking.model.Room;
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

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoomServiceTest {

    @Mock
    private RoomRepository roomRepository;
    private RoomService roomService;

    private Room room;
    private Pageable pageable;
    private Page<Room> rooms, noRooms;

    @BeforeEach
    public void setUp() {
        roomService = new RoomService(this.roomRepository);
        room = Room.builder()
                .withNumber(1)
                .withFloor(1)
                .withRooms(1)
                .build();
        pageable = PageRequest.of(0,10);
        rooms = new PageImpl<>(Collections.singletonList(room), Pageable.ofSize(1), 0);
        noRooms = new PageImpl<>(Collections.emptyList(), Pageable.unpaged(), 0);
    }

    @Test
    public void createRoom() {
        when(roomRepository.save(room)).thenReturn(room);

        Room createdRoom = roomService.create(room);
        assertThat(createdRoom).isEqualTo(room);
    }

    @Test
    public void getByRoomNumber(){
        when(roomRepository.findById(1)).thenReturn(Optional.of(room));

        Optional<Room> mayBeRoom = roomService.getByRoomNumber(1);
        assertThat(mayBeRoom.isEmpty()).isFalse();
    }

    @Test
    public void getByNonExistingRoomNumber(){
        when(roomRepository.findById(1)).thenReturn(Optional.empty());

        Optional<Room> mayBeRoom = roomService.getByRoomNumber(1);
        assertThat(mayBeRoom.isEmpty()).isTrue();
    }

    @Test
    public void removeByRoomNumber(){
        when(roomRepository.existsById(1)).thenReturn(true);
        boolean removed = roomService.removeByRoomNumber(1);
        assertThat(removed).isTrue();
    }

    @Test
    public void removeByNonExistingRoomNumber(){
        when(roomRepository.existsById(1)).thenReturn(false);
        boolean removed = roomService.removeByRoomNumber(1);
        assertThat(removed).isFalse();
    }

    @Test
    public void modifyRoom(){
        final Room modifiedRoom = Room.builder()
                .withNumber(1)
                .withFloor(2)
                .withRooms(3)
                .build();
        when(roomRepository.findById(1)).thenReturn(Optional.of(room));
        when(roomRepository.save(modifiedRoom)).thenReturn(modifiedRoom);

        Optional<Room> mayBeModifiedRoom = roomService.modify(1, modifiedRoom);
        assertThat(mayBeModifiedRoom.isPresent()).isTrue();
    }

    @Test
    public void modifyNonExistentRoom(){
        final Room modifiedRoom = Room.builder()
                .withNumber(1)
                .withFloor(2)
                .withRooms(3)
                .build();
        when(roomRepository.findById(1)).thenReturn(Optional.empty());

        Optional<Room> mayBeModifiedRoom = roomService.modify(1, modifiedRoom);
        assertThat(mayBeModifiedRoom.isEmpty()).isTrue();
    }

    @Test
    public void listRooms(){
        when(roomRepository.findAll(pageable))
                .thenReturn(rooms);

        Page<Room> foundRooms = roomService.listAll(pageable);
        assertThat(foundRooms.getTotalElements()).isEqualTo(1);
    }

    @Test
    public void listNoRooms(){
        when(roomRepository.findAll(pageable))
                .thenReturn(noRooms);

        Page<Room> foundRooms = roomService.listAll(pageable);
        assertThat(foundRooms.getTotalElements()).isEqualTo(0);
    }

}