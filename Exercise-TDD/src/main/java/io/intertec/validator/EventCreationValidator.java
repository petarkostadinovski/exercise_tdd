package io.intertec.validator;

import io.intertec.exceptions.InvalidEventEndDateException;
import io.intertec.exceptions.InvalidEventStartDateException;
import io.intertec.model.record.EventCreationRequestRecord;

import java.time.LocalDate;
import java.time.ZoneId;

public class EventCreationValidator {

    public static void validateEventStartEndDate(EventCreationRequestRecord eventCreationRecord) {
        boolean isEventCreationDateBeforeToday = eventCreationRecord.startDate().atZone(ZoneId.systemDefault()).toLocalDate()
                .isBefore(LocalDate.now());

        boolean isEndDateAfterStartDate = eventCreationRecord.endDate().isBefore(eventCreationRecord.startDate());

        if (isEventCreationDateBeforeToday) {
            throw new InvalidEventStartDateException();
        }

        if (isEndDateAfterStartDate) {
            throw new InvalidEventEndDateException();
        }
    }

}
