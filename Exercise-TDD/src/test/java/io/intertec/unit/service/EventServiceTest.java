package io.intertec.unit.service;

import io.intertec.exceptions.InvalidEventEndDateException;
import io.intertec.exceptions.InvalidEventStartDateException;
import io.intertec.model.Event;
import io.intertec.model.record.EventCreationRequestRecord;
import io.intertec.model.record.EventResponseRecord;
import io.intertec.model.record.EventUpdateRequestRecord;
import io.intertec.model.record.IdRecord;
import io.intertec.repository.EventRepository;
import io.intertec.service.EventService;
import io.intertec.service.mapper.EventMapper;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@SpringBootTest
public class EventServiceTest {

    @InjectMocks
    EventService eventService;

    @Mock
    EventRepository eventRepository;

    @Mock
    EventMapper eventMapper;

    @Test
    void throwsInvalidEventStartDate_WhenCreatingEventWithStartDateBeforeToday() {
        // arrange
        Instant startDate = Instant.now().minus(1, ChronoUnit.DAYS);
        Instant endDate = startDate.plus(5, ChronoUnit.DAYS);
        EventCreationRequestRecord eventCreationRecord = new EventCreationRequestRecord("Test name", startDate, endDate);

        // act & assert
        assertThrows(
                InvalidEventStartDateException.class,
                () -> eventService.createEvent(eventCreationRecord)
        );
    }

    @Test
    void throwsInvalidEventEndDate_WhenCreatingEventWithEndDateBeforeStartDate() {
        // arrange
        Instant startDate = Instant.now();
        Instant endDate = startDate.minus(1, ChronoUnit.MINUTES);
        EventCreationRequestRecord eventCreationRecord = new EventCreationRequestRecord("Test name", startDate, endDate);

        // act & assert
        assertThrows(
                InvalidEventEndDateException.class,
                () -> eventService.createEvent(eventCreationRecord)
        );
    }

    @Test
    void entityCreationId_WhenCreatingEvent() {
        // arrange
        String name = "Test name";
        Instant startDate = Instant.now();
        Instant endDate = startDate.plus(1, ChronoUnit.MINUTES);
        EventCreationRequestRecord eventCreationRecord = new EventCreationRequestRecord(name, startDate, endDate);

        Event mappedEvent = new Event(null, eventCreationRecord.name(), eventCreationRecord.startDate(), eventCreationRecord.endDate());
        Event createdEvent = new Event(1, name, startDate, endDate);

        // act
        when(eventMapper.mapFromEventCreationRecordToEvent(eventCreationRecord)).thenReturn(mappedEvent);
        when(eventRepository.save(any(Event.class))).thenReturn(createdEvent);

        IdRecord eventIdRecord = eventService.createEvent(eventCreationRecord);

        // assert
        assertEquals(1, eventIdRecord.id());
    }

    @Test
    void fetchEvents() {
        // arrange
        Integer id = 1;
        String name = "Test name";
        Instant startDate = Instant.now();
        Instant endDate = startDate.plus(1, ChronoUnit.DAYS);
        EventResponseRecord eventRecord = new EventResponseRecord(
                id, name, startDate, endDate
        );
        List<EventResponseRecord> eventResponseList = List.of(eventRecord);
        Event event = new Event(id, name, startDate, endDate);
        List<Event> events = List.of(event);

        // act
        when(eventRepository.findAll()).thenReturn(events);
        when(eventMapper.mapFromEventToEventResponseRecord(event)).thenReturn(eventResponseList.get(0));

        List<EventResponseRecord> fetchedEvents = eventService.getEvents();

        // assert
        assertEquals(eventResponseList, fetchedEvents);
    }

    @Test
    void deleteById() {
        // arrange
        Integer id = 1;

        // assert
        eventService.deleteById(id);

        // act
        verify(eventRepository).deleteById(id);
    }

    @Test
    void updateStartDate_WhenEventIsNotStarted() {
        // arrange
        Event eventMock = mock(Event.class);
        EventUpdateRequestRecord eventUpdateRequestRecord = getEventUpdateRequestRecord();

        // act
        when(eventRepository.getReferenceById(anyInt())).thenReturn(eventMock);
        when(eventMock.getStartDate()).thenReturn(Instant.now().plus(1, ChronoUnit.HOURS));
        when(eventMock.getEndDate()).thenReturn(eventUpdateRequestRecord.endDate());

        eventService.updateEvent(anyInt(), eventUpdateRequestRecord);

        // assert
        verify(eventMock).setStartDate(eventUpdateRequestRecord.startDate());
    }

    @Test
    void doNotUpdateStartDate_WhenEventIsStarted() {
        // arrange
        Event eventMock = mock(Event.class);
        EventUpdateRequestRecord eventUpdateRequestRecord = getEventUpdateRequestRecord();

        // act
        when(eventRepository.getReferenceById(anyInt())).thenReturn(eventMock);
        when(eventMock.getStartDate()).thenReturn(Instant.now().minus(1, ChronoUnit.HOURS));
        when(eventMock.getEndDate()).thenReturn(eventUpdateRequestRecord.endDate());

        eventService.updateEvent(anyInt(), eventUpdateRequestRecord);

        // assert
        verify(eventMock, never()).setStartDate(eventUpdateRequestRecord.startDate());
    }

    @Test
    void updateEndDate_WhenEventIsNotFinished() {
        // arrange
        Event eventMock = mock(Event.class);
        EventUpdateRequestRecord eventUpdateRequestRecord = getEventUpdateRequestRecord();

        // act
        when(eventRepository.getReferenceById(anyInt())).thenReturn(eventMock);
        when(eventMock.getStartDate()).thenReturn(eventUpdateRequestRecord.endDate());
        when(eventMock.getEndDate()).thenReturn(Instant.now().plus(1, ChronoUnit.HOURS));

        eventService.updateEvent(anyInt(), eventUpdateRequestRecord);

        // assert
        verify(eventMock).setEndDate(eventUpdateRequestRecord.endDate());
    }

    @Test
    void doNotUpdateEndDate_WhenEventIsFinished() {
        // arrange
        Event eventMock = mock(Event.class);
        EventUpdateRequestRecord eventUpdateRequestRecord = getEventUpdateRequestRecord();

        // act
        when(eventRepository.getReferenceById(anyInt())).thenReturn(eventMock);
        when(eventMock.getStartDate()).thenReturn(eventUpdateRequestRecord.endDate());
        when(eventMock.getEndDate()).thenReturn(Instant.now().minus(1, ChronoUnit.HOURS));

        eventService.updateEvent(anyInt(), eventUpdateRequestRecord);

        // assert
        verify(eventMock, never()).setEndDate(eventUpdateRequestRecord.endDate());
    }

    @Test
    void updateName() {
        // arrange
        Event eventMock = mock(Event.class);
        EventUpdateRequestRecord eventUpdateRequestRecord = getEventUpdateRequestRecord();

        // act
        when(eventRepository.getReferenceById(anyInt())).thenReturn(eventMock);
        when(eventMock.getStartDate()).thenReturn(eventUpdateRequestRecord.endDate());
        when(eventMock.getEndDate()).thenReturn(Instant.now().minus(1, ChronoUnit.HOURS));

        eventService.updateEvent(anyInt(), eventUpdateRequestRecord);

        // assert
        verify(eventMock).setName(eventUpdateRequestRecord.name());
    }


    EventUpdateRequestRecord getEventUpdateRequestRecord() {
        String name = "Test Name";
        Instant startDate = Instant.now();
        Instant endDate = startDate.plus(5, ChronoUnit.DAYS);
        return new EventUpdateRequestRecord(name, startDate, endDate);
    }


}
