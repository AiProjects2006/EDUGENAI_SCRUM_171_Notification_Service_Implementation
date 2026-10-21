
package com.ailearning.notificationservice.consumer;

import com.ailearning.notificationservice.event.NotificationEvent;
import com.ailearning.notificationservice.service.NotificationProcessingService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class NotificationEventConsumer {

    private static final Logger log =
            LoggerFactory.getLogger(NotificationEventConsumer.class);

    private final ObjectMapper objectMapper;
    private final NotificationProcessingService processingService;

    public NotificationEventConsumer(
            ObjectMapper objectMapper,
            NotificationProcessingService processingService) {
        this.objectMapper = objectMapper;
        this.processingService = processingService;
    }

    @KafkaListener(
            topics = "${notification.kafka.topic}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consume(String payload) {

        // Reject an empty Kafka message before attempting JSON parsing.
        if (payload == null || payload.isBlank()) {
            log.warn("Rejected empty notification event");
            return;
        }

        final NotificationEvent event;

        try {
            event = objectMapper.readValue(
                    payload, NotificationEvent.class);
        } catch (JsonProcessingException ex) {
            log.error("Rejected invalid notification JSON: {}",
                    ex.getOriginalMessage());
            return;
        }

        if (event == null || !event.hasMinimumRequiredFields()) {
            log.warn(
                    "Rejected notification event: required fields are missing");
            return;
        }

        // Do not swallow processing failures. Allow Kafka's configured
        // error handler to apply its retry/recovery policy.
        processingService.process(event);

        log.info(
                "Notification event processed. eventId={}, eventType={}",
                event.eventId(),
                event.eventType()
        );
    }
}
