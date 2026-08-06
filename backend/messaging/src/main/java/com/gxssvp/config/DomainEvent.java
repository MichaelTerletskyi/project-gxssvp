package com.gxssvp.config;

import java.time.Instant;
import java.util.UUID;
import lombok.Getter;

/**
 * Basic contract for all domain events.
 * eventId и occurredAt — is a standard for event-driven systems.
 * eventId for idempotency for consumer side (to avoid processing the same event twice).
 * occurredAt - for tracing and debug.
 *
 * @author Michael Terletskyi
 */
@Getter
public abstract class DomainEvent {
    private final UUID eventId = UUID.randomUUID();
    private final Instant occurredAt = Instant.now();
}