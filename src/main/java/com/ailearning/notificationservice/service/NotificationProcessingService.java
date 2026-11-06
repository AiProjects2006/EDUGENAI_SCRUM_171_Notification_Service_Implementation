
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

    private static final Logger log =
            LoggerFactory.getLogger(NotificationProcessingService.class);

    private final NotificationDelivery notificationDelivery;

    private final NotificationEmailDeliveryService emailDeliveryService;

    public NotificationProcessingService(
            NotificationDelivery notificationDelivery,
            NotificationEmailDeliveryService emailDeliveryService) {

        this.notificationDelivery = notificationDelivery;
        this.emailDeliveryService = emailDeliveryService;
    }

    public void process(NotificationEvent event) {

        if (event == null || !event.hasMinimumRequiredFields()) {
            throw new IllegalArgumentException(
                    "eventType and message are required"
            );
        }

        String subject = (event.subject() == null ||
                event.subject().isBlank())
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

        // 1. Save or deliver using the existing delivery mechanism.
        notificationDelivery.deliver(notification);

        // 2. Send an email if a recipient email address is available.
        if (event.recipientEmail() != null &&
                !event.recipientEmail().isBlank()) {

            emailDeliveryService.sendNotificationEmail(
                    event.recipientEmail(),
                    subject,
                    event.message()
            );

            log.info(
                    "Email notification sent through Email API. eventId={}",
                    event.eventId()
            );

        } else {
            log.info(
                    "Email delivery skipped because recipient email is missing. eventId={}",
                    event.eventId()
            );
        }

        log.info(
                "Notification handled. eventType={}, eventId={}",
                event.eventType(),
                event.eventId()
        );
    }
}
