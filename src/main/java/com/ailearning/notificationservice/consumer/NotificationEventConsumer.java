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
    private static final Logger log = LoggerFactory.getLogger(NotificationEventConsumer.class);

    private final ObjectMapper objectMapper;
    private final NotificationProcessingService processingService;

    public NotificationEventConsumer(ObjectMapper objectMapper,
                                     NotificationProcessingService processingService) {
        this.objectMapper = objectMapper;
        this.processingService = processingService;
    }

    @KafkaListener(topics = "${notification.kafka.topic}")
    public void consume(String payload) {
        final NotificationEvent event;
        try {
            event = objectMapper.readValue(payload, NotificationEvent.class);
        } catch (JsonProcessingException ex) {
            // Production deployments should configure a dead-letter topic / error handler.
            log.error("Unable to deserialize notification event; payload rejected. reason={}",
                    ex.getOriginalMessage());
            return;
        }

        if (event == null || !event.hasMinimumRequiredFields()) {
            log.warn("Notification event rejected because required fields are missing");
            return;
        }

        processingService.process(event);
    }
}
