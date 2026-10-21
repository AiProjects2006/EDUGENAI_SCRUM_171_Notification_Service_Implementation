package com.ailearning.notificationservice.event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.Instant;
import java.util.Map;

/**
 * Starter event contract. Confirm field names and schema with the producer team.
 * Unknown JSON properties are ignored to make local integration less brittle,
 * but the agreed production schema should be documented and validated.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record NotificationEvent(
        String eventId,
        String eventType,
        String recipientId,
        String recipientEmail,
        String subject,
        String message,
        Instant occurredAt,
        Map<String, Object> data
) {
    public boolean hasMinimumRequiredFields() {
        return eventType != null && !eventType.isBlank()
                && message != null && !message.isBlank();
    }
}
