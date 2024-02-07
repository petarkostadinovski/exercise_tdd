package io.intertec.model.record;

import jakarta.validation.constraints.NotNull;

import java.time.Instant;


public record EventCreationRequestRecord(@NotNull String name, @NotNull Instant startDate, @NotNull Instant endDate) {
}
