
package com.ailearning.notificationservice.event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.Instant;
import java.util.Map;

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
