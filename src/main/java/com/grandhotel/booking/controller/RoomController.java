package com.grandhotel.booking.controller;

import com.grandhotel.booking.model.Room;
import com.grandhotel.booking.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/room")
@RequiredArgsConstructor
public class RoomController {
    private final RoomService roomService;
    private final Integer pageSize;

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public Room create(@RequestBody final Room room, final HttpServletResponse response){
        response.addHeader(HttpHeaders.LOCATION, String.format("/room/%d", room.getNumber()));
        return roomService.create(room);
    }

    @PutMapping("/{roomNumber}")
    public Room modify(@PathVariable final Integer roomNumber, @RequestBody final Room room){
        final Optional<Room> mayBeRoom =  roomService.modify(roomNumber, room);

        if(mayBeRoom.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        return mayBeRoom.get();
    }

    @GetMapping("/{roomNumber}")
    public Room get(@PathVariable final Integer roomNumber){
        final Optional<Room> mayBeRoom = roomService.getByRoomNumber(roomNumber);

        if(mayBeRoom.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        return mayBeRoom.get();
    }

    @DeleteMapping("/{roomNumber}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void delete(@PathVariable final Integer roomNumber){
        final boolean deleteOk = roomService.removeByRoomNumber(roomNumber);
        if(!deleteOk)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    @GetMapping
    public Page<Room> list(@RequestParam(value = "page", required = false, defaultValue = "1") final Integer page){
        if(page<1)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Parameter page should be greater than 0");
        return roomService.listAll(PageRequest.of(page - 1, pageSize));
    }
}
