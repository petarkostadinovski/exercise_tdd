package io.intertec.integration;

import io.intertec.ExerciseTddApplication;
import io.intertec.model.record.EventResponseRecord;
import io.intertec.model.record.EventCreationRequestRecord;
import io.intertec.model.record.EventUpdateRequestRecord;
import io.intertec.model.record.IdRecord;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;

import java.net.URI;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = ExerciseTddApplication.class
)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class EventControllerIT {

    @Autowired
    TestRestTemplate testRestTemplate;

    @Test
    @SqlGroup({
            @Sql(scripts = "classpath:reset_event_table.sql"),
            @Sql(scripts = "classpath:insert_event.sql")
    })
    void status200_WhenFetchingEvents() {
        // arrange
        EventResponseRecord expectedEvent = new EventResponseRecord(
                1, "Test event", Instant.parse("2024-05-02T14:30:00Z"), Instant.parse("2024-06-02T14:30:00Z")
        );

        // act
        ResponseEntity<List<EventResponseRecord>> response = testRestTemplate.exchange(
                URI.create("/api/events"),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );

        // assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(expectedEvent, response.getBody().get(0));
    }

    @Test
    void status200_WhenCreatingEvent() throws JSONException {
        // arrange
        String name = "Test Name";
        Instant startDate = Instant.now();
        Instant endDate = startDate.plus(5, ChronoUnit.DAYS);
        EventCreationRequestRecord eventCreationRecord = new EventCreationRequestRecord(name, startDate, endDate);

        // act
        ResponseEntity<IdRecord> response = testRestTemplate.postForEntity(
                URI.create("/api/events"),
                eventCreationRecord,
                IdRecord.class
        );

        // assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().id());
    }

    @Test
    void status200_WhenDeletingEventById() {
        // arrange
        int id = 1;

        // act
        ResponseEntity<Void> response = testRestTemplate.exchange(
                URI.create("/api/events/" + id),
                HttpMethod.DELETE,
                null,
                Void.class
        );

        // assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @SqlGroup({
            @Sql(scripts = "classpath:reset_event_table.sql"),
            @Sql(scripts = "classpath:insert_event.sql")
    })
    void status200_WhenUpdatingEvent() {
        // arrange
        int id = 1;
        String name = "Test Name";
        Instant startDate = Instant.now();
        Instant endDate = startDate.plus(5, ChronoUnit.DAYS);
        EventUpdateRequestRecord eventUpdateRequestRecord = new EventUpdateRequestRecord(name, startDate, endDate);

        // act
        ResponseEntity<Void> response = testRestTemplate.exchange(
                URI.create("/api/events/" + id),
                HttpMethod.PUT,
                new HttpEntity<>(eventUpdateRequestRecord),
                Void.class
        );

        // assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    private void save() {
        String name = "Test Name";
        Instant startDate = Instant.now();
        Instant endDate = startDate.plus(5, ChronoUnit.DAYS);
        EventCreationRequestRecord eventCreationRecord = new EventCreationRequestRecord(name, startDate, endDate);
        ResponseEntity<IdRecord> response = testRestTemplate.postForEntity(
                URI.create("/api/events"),
                eventCreationRecord,
                IdRecord.class
        );
    }

}
