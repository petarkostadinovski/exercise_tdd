package io.intertec.model.record;

import java.time.Instant;

public record EventUpdateRequestRecord(String name, Instant startDate, Instant endDate) {
}
