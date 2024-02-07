package io.intertec.unit.repository;

import io.intertec.model.Event;
import io.intertec.repository.EventRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class EventRepositoryTest {

    @Autowired
    EventRepository eventRepository;

    @Test
    void entity_WhenSavingNewEvent() {
        // arrange
        String name = "Test name";
        Instant startDate = Instant.now();
        Instant endDate = startDate.plus(1, ChronoUnit.MINUTES);
        Event event = Event.builder()
                .name(name)
                .startDate(startDate)
                .endDate(endDate)
                .build();

        // act
        Event createdEvent = eventRepository.save(event);

        // assert
        assertNotNull(createdEvent.getId());
        assertEquals(event.getName(), createdEvent.getName());
        assertEquals(event.getStartDate(), createdEvent.getStartDate());
        assertEquals(event.getEndDate(), createdEvent.getEndDate());
    }

}
