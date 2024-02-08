package io.intertec.unit.controller;

import io.intertec.model.record.EventCreationRequestRecord;
import io.intertec.model.record.EventResponseRecord;
import io.intertec.model.record.EventUpdateRequestRecord;
import io.intertec.model.record.IdRecord;
import io.intertec.service.EventService;
import org.apache.catalina.User;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@AutoConfigureMockMvc(addFilters = false)
public class EventControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    EventService eventService;

    @Test
    void creationIdAndStatus200_WhenCreatingEvent() throws Exception {
        // arrange
        String name = "Test Name";
        Instant startDate = Instant.now();
        Instant endDate = startDate.plus(5, ChronoUnit.DAYS);
        String jsonRequestBody = new JSONObject()
                .put("name", name)
                .put("startDate", startDate.toString())
                .put("endDate", endDate.toString())
                .toString();

        IdRecord eventCreationResponse = new IdRecord(1);

        // act & assert
        when(eventService.createEvent(new EventCreationRequestRecord(name, startDate, endDate))).thenReturn(eventCreationResponse);
        mockMvc.perform(post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(1));
    }

    @Test
    void status400_WhenCreatingEventWithMissingNameParam() throws Exception {
        // arrange
        Instant startDate = Instant.now();
        Instant endDate = startDate.plus(5, ChronoUnit.DAYS);
        String jsonRequestBody = new JSONObject()
                .put("startDate", startDate.toString())
                .put("endDate", endDate.toString())
                .toString();

        // act & assert
        mockMvc.perform(
                post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestBody)
        ).andExpect(status().isBadRequest());
    }

    @Test
    void status400_WhenCreatingEventWithMissingStartDateParam() throws Exception {
        // arrange
        String name = "Test Name";
        Instant endDate = Instant.now().plus(5, ChronoUnit.DAYS);
        String jsonRequestBody = new JSONObject()
                .put("name", name)
                .put("endDate", endDate.toString())
                .toString();

        // act & assert
        mockMvc.perform(
                post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestBody)
        ).andExpect(status().isBadRequest());
    }

    @Test
    void status400_WhenCreatingEventWithMissingEndDateParam() throws Exception {
        // arrange
        String name = "Test Name";
        Instant endDate = Instant.now().plus(5, ChronoUnit.DAYS);
        String jsonRequestBody = new JSONObject()
                .put("name", name)
                .put("endDate", endDate.toString())
                .toString();

        // act & assert
        mockMvc.perform(
                post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestBody)
        ).andExpect(status().isBadRequest());
    }

    @Test
    void eventsAndStatus200_WhenFetchingEvents() throws Exception {
        // arrange
        Integer id = 1;
        String name = "Test name";
        Instant startDate = Instant.now();
        Instant endDate = startDate.plus(1, ChronoUnit.DAYS);
        EventResponseRecord event = new EventResponseRecord(
                id, name, startDate, endDate
        );
        List<EventResponseRecord> eventsResponseList = List.of(event);

        // act
        when(eventService.getEvents()).thenReturn(eventsResponseList);

        // assert
        mockMvc.perform(get("/api/events"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("[0].id").value(id))
                .andExpect(jsonPath("[0].name").value(name))
                .andExpect(jsonPath("[0].startDate").value(startDate.toString()))
                .andExpect(jsonPath("[0].endDate").value(endDate.toString()));
    }

    @Test
    void status200_WhenDeletingEvent() throws Exception {
        // arrange
        Integer id = 1;

        //act & assert
        mockMvc.perform(delete("/api/events/" + id))
                .andExpect(status().isOk());

        verify(eventService).deleteById(id);
    }

    @Test
    void status200_WhenUpdatingEvent() throws Exception {
        // arrange
        int id = 1;
        String name = "Test Name";
        Instant startDate = Instant.now();
        Instant endDate = startDate.plus(5, ChronoUnit.DAYS);
        String jsonRequestBody = new JSONObject()
                .put("name", name)
                .put("startDate", startDate.toString())
                .put("endDate", endDate.toString())
                .toString();
        EventUpdateRequestRecord eventUpdateRequestRecord = new EventUpdateRequestRecord(name, startDate, endDate);

        // act & assert
        mockMvc.perform(
                    put("/api/events/" + id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonRequestBody)
                ).andExpect(status().isOk());

        verify(eventService).updateEvent(id, eventUpdateRequestRecord);
    }

}
