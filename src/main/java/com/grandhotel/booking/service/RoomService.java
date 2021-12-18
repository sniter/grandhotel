package com.grandhotel.booking.service;

import com.grandhotel.booking.model.Room;
import com.grandhotel.booking.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service @RequiredArgsConstructor
public class RoomService {
    private final RoomRepository roomRepository;

    public Room create(final Room room) {
        return roomRepository.save(room);
    }

    public Optional<Room> getByRoomNumber(final Integer roomNumber) {
        return roomRepository.findById(roomNumber);
    }

    public boolean removeByRoomNumber(final Integer roomNumber) {
        if(!roomRepository.existsById(roomNumber))
            return false;
        roomRepository.deleteById(roomNumber);
        return true;
    }

    public Page<Room> listAll(final Pageable pageable) {
        return roomRepository.findAll(pageable);
    }

    public Optional<Room> modify(final Integer roomNumber, final Room newRoom) {
        return roomRepository.findById(roomNumber).map((room) -> {
            room.setRooms(newRoom.getRooms());
            room.setFloor(newRoom.getFloor());
            return room;
        }).map(roomRepository::save);
    }
}
