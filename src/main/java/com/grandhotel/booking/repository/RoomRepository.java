package com.grandhotel.booking.repository;

import com.grandhotel.booking.model.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;

public interface RoomRepository extends JpaRepository<Room, Integer> {
    Page<Room> findByNumberNotIn(Collection<Integer> numbers, Pageable pageable);
}
