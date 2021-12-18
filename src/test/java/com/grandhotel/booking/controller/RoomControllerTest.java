package com.grandhotel.booking.controller;

import com.grandhotel.booking.model.Reservation;
import com.grandhotel.booking.model.Room;
import com.grandhotel.booking.service.RoomService;
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

import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RoomController.class)
@Import(ControllersConfig.class)
class RoomControllerTest {

    @MockBean
    private RoomService roomService;

    @Autowired
    private MockMvc mockMvc;

    private String urlPrefix;
    private String roomJsonRepr, roomModifyJsonRepr;
    private Room room;
    private Pageable pageable;
    private Page<Room> rooms;
    private Page<Room> emptyRooms;

    @BeforeEach
    void setup(){
        urlPrefix = "/room";
        roomJsonRepr = "{\"number\": 1, \"rooms\": 1, \"floor\": 1}";
        roomModifyJsonRepr = "{\"number\": 1, \"rooms\": 2, \"floor\": 2}";
        room = Room.builder().withNumber(1).withRooms(1).withFloor(1).build();
        pageable = PageRequest.of(0,10);
        rooms = new PageImpl<>(Collections.singletonList(room), pageable, 1);
        emptyRooms = new PageImpl<>(Collections.emptyList(), pageable, 0);
    }

    @Test
    public void createRoom() throws Exception {
        when(roomService.create(room)).thenReturn(room);

        mockMvc.perform(post(urlPrefix)
                .contentType(MediaType.APPLICATION_JSON)
                .content(roomJsonRepr))
                .andExpect(status().isCreated())
                .andExpect(header().string(HttpHeaders.LOCATION, urlPrefix + "/1"));

    }

    @Test
    public void modifyExistingRoom() throws Exception {
        Room modifyRoom = Room.builder().withNumber(1).withFloor(2).withRooms(2).build();
        when(roomService.modify(anyInt(), any(Room.class))).thenReturn(Optional.of(modifyRoom));

        mockMvc.perform(put(urlPrefix + "/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(roomModifyJsonRepr))
                .andExpect(status().isOk());
    }

    @Test
    public void modifyNonExistingRoom() throws Exception {
        Room modifyRoom = Room.builder().withNumber(2).withFloor(2).withRooms(2).build();
        when(roomService.modify(room.getNumber(), modifyRoom)).thenReturn(Optional.empty());

        mockMvc.perform(put(urlPrefix + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(roomModifyJsonRepr))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getExistingRoom() throws Exception {
        when(roomService.getByRoomNumber(room.getNumber())).thenReturn(Optional.of(room));

        mockMvc.perform(get(urlPrefix + "/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void getNonExistingRoom() throws Exception {
        when(roomService.getByRoomNumber(room.getNumber())).thenReturn(Optional.empty());

        mockMvc.perform(get(urlPrefix + "/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void deleteExistingRoom() throws Exception {
        when(roomService.removeByRoomNumber(room.getNumber())).thenReturn(true);

        mockMvc.perform(delete(urlPrefix + "/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    public void deleteNonExistingRoom() throws Exception {
        when(roomService.removeByRoomNumber(room.getNumber())).thenReturn(false);

        mockMvc.perform(delete(urlPrefix + "/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void listAllRooms() throws Exception {
        when(roomService.listAll(pageable)).thenReturn(rooms);

        mockMvc.perform(get(urlPrefix).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void listRoomsWithEmptyPage() throws Exception {
        when(roomService.listAll(pageable)).thenReturn(emptyRooms);

        mockMvc.perform(get(urlPrefix + "?page=2").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void listRoomsWithInvalidPage() throws Exception {
        when(roomService.listAll(pageable)).thenReturn(emptyRooms);

        mockMvc.perform(get(urlPrefix + "?page=-2").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

}