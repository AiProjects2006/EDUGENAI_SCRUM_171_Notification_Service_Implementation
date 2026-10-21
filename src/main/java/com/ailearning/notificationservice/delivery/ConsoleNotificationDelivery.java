package com.ailearning.notificationservice.delivery;

import com.ailearning.notificationservice.model.Notification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Development-only delivery adapter.
 * Replace or supplement this adapter with the team's agreed email or in-app channel.
 */
@Component
@ConditionalOnProperty(name = "notification.delivery.mode", havingValue = "console", matchIfMissing = true)
public class ConsoleNotificationDelivery implements NotificationDelivery {
    private static final Logger log = LoggerFactory.getLogger(ConsoleNotificationDelivery.class);

    @Override
    public void deliver(Notification notification) {
        // Do not log recipient email or message body in production.
        log.info("DEV notification output: eventType={}, eventId={}, recipientId={}, subject={}",
                notification.eventType(), notification.eventId(),
                notification.recipientId(), notification.subject());
    }
}
