package io.intertec.service;

import io.intertec.model.Event;
import io.intertec.model.record.EventCreationRequestRecord;
import io.intertec.model.record.EventResponseRecord;
import io.intertec.model.record.EventUpdateRequestRecord;
import io.intertec.model.record.IdRecord;
import io.intertec.repository.EventRepository;
import io.intertec.service.mapper.EventMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.time.Instant;
import java.util.List;

import static io.intertec.validator.EventCreationValidator.validateEventStartEndDate;

@Service
@RequiredArgsConstructor
public class EventService {

    final EventRepository eventRepository;

    final EventMapper eventMapper;

    public IdRecord createEvent(EventCreationRequestRecord eventCreationRecord) {
        validateEventStartEndDate(eventCreationRecord);

        Event event = eventMapper.mapFromEventCreationRecordToEvent(eventCreationRecord);

        Event createdEvent = eventRepository.save(event);

        return new IdRecord(createdEvent.getId());
    }

    public List<EventResponseRecord> getEvents() {
        return eventRepository.findAll()
                .stream()
                .map(eventMapper::mapFromEventToEventResponseRecord)
                .toList();
    }

    public void deleteById(Integer id) {
        eventRepository.deleteById(id);
    }

    public void updateEvent(Integer id, EventUpdateRequestRecord eventUpdateRequestRecord) {
        Event event = eventRepository.getReferenceById(id);

        event.setName(eventUpdateRequestRecord.name());

        if (event.getStartDate().isAfter(Instant.now()))
            event.setStartDate(eventUpdateRequestRecord.startDate());

        if (event.getEndDate().isAfter(Instant.now()))
            event.setEndDate(eventUpdateRequestRecord.endDate());
    }
}
