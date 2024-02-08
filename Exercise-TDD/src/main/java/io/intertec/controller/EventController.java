package io.intertec.controller;

import io.intertec.model.record.EventResponseRecord;
import io.intertec.model.record.EventCreationRequestRecord;
import io.intertec.model.record.EventUpdateRequestRecord;
import io.intertec.model.record.IdRecord;
import io.intertec.service.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    final EventService eventService;

    @PostMapping
    IdRecord createEvent(@RequestBody @Valid EventCreationRequestRecord eventCreationRecord) {
        return eventService.createEvent(eventCreationRecord);
    }

    @GetMapping
    public List<EventResponseRecord> getEvents() {
        return eventService.getEvents();
    }

    @DeleteMapping("/{id}")
    void deleteEvent(@PathVariable Integer id) {
        eventService.deleteById(id);
    }

    @PutMapping("/{id}")
    void updateEvent(@PathVariable Integer id,
                     @RequestBody EventUpdateRequestRecord eventUpdateRequestRecord) {
        eventService.updateEvent(id, eventUpdateRequestRecord);
    }

}
