package io.intertec.service.mapper;

import io.intertec.model.Event;
import io.intertec.model.record.EventCreationRequestRecord;
import io.intertec.model.record.EventResponseRecord;
import org.springframework.stereotype.Component;

@Component
public class EventMapper {

    public Event mapFromEventCreationRecordToEvent(EventCreationRequestRecord eventCreationRecord) {
        return Event.builder()
                .name(eventCreationRecord.name())
                .startDate(eventCreationRecord.startDate())
                .endDate(eventCreationRecord.endDate())
                .build();
    }

    public EventResponseRecord mapFromEventToEventResponseRecord(Event event) {
        return new EventResponseRecord(event.getId(), event.getName(), event.getStartDate(), event.getEndDate());
    }
}
