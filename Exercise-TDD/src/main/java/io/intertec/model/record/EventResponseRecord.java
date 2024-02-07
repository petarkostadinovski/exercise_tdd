package io.intertec.model.record;

import java.time.Instant;

public record EventResponseRecord(Integer id, String name, Instant startDate, Instant endDate) {
}
