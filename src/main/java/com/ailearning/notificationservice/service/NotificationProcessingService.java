package com.ailearning.notificationservice.service;

import com.ailearning.notificationservice.event.NotificationEvent;
import com.ailearning.notificationservice.model.Notification;
import com.ailearning.notificationservice.delivery.NotificationDelivery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class NotificationProcessingService {
    private static final Logger log = LoggerFactory.getLogger(NotificationProcessingService.class);

    private final NotificationDelivery notificationDelivery;

    public NotificationProcessingService(NotificationDelivery notificationDelivery) {
        this.notificationDelivery = notificationDelivery;
    }

    public void process(NotificationEvent event) {
        if (!event.hasMinimumRequiredFields()) {
            throw new IllegalArgumentException("eventType and message are required");
        }

        String subject = (event.subject() == null || event.subject().isBlank())
                ? "Notification: " + event.eventType()
                : event.subject();

        Notification notification = new Notification(
                event.eventId(),
                event.eventType(),
                event.recipientId(),
                event.recipientEmail(),
                subject,
                event.message(),
                Instant.now()
        );

        notificationDelivery.deliver(notification);
        log.info("Notification handled. eventType={}, eventId={}",
                event.eventType(), event.eventId());
    }
}
