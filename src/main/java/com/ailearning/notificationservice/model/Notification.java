package com.ailearning.notificationservice.model;

import java.time.Instant;

public record Notification(
        String eventId,
        String eventType,
        String recipientId,
        String recipientEmail,
        String subject,
        String message,
        Instant createdAt
) {
}
