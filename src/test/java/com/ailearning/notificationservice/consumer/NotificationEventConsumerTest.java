
package com.ailearning.notificationservice.consumer;

import com.ailearning.notificationservice.event.NotificationEvent;
import com.ailearning.notificationservice.service.NotificationProcessingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

class NotificationEventConsumerTest {

    private NotificationProcessingService processingService;
    private NotificationEventConsumer consumer;

    @BeforeEach
    void setUp() {
        processingService = mock(NotificationProcessingService.class);

        consumer = new NotificationEventConsumer(
                new ObjectMapper().findAndRegisterModules(),
                processingService
        );
    }

    @Test
    void validEventIsPassedToProcessingService() {
        String payload = """
                {
                  "eventId": "scrum171-test-001",
                  "eventType": "COURSE_CREATED",
                  "recipientId": "student-123",
                  "subject": "New course",
                  "message": "A new course is available",
                  "data": {}
                }
                """;

        consumer.consume(payload);

        verify(processingService).process(argThat(
                (NotificationEvent event) ->
                        "scrum171-test-001".equals(event.eventId())
                        && "COURSE_CREATED".equals(event.eventType())
                        && "student-123".equals(event.recipientId())
                        && "A new course is available".equals(event.message())
        ));
    }

    @Test
    void malformedJsonIsRejected() {
        consumer.consume("{invalid-json");

        verifyNoInteractions(processingService);
    }

    @Test
    void emptyPayloadIsRejected() {
        consumer.consume(" ");

        verifyNoInteractions(processingService);
    }

    @Test
    void eventMissingRequiredFieldsIsRejected() {
        String payload = """
                {
                  "eventId": "scrum171-test-002",
                  "eventType": "COURSE_CREATED",
                  "message": " "
                }
                """;

        consumer.consume(payload);

        verifyNoInteractions(processingService);
    }

    @Test
    void processingFailureIsPropagated() {
        String payload = """
                {
                  "eventId": "scrum171-test-003",
                  "eventType": "COURSE_CREATED",
                  "recipientId": "student-123",
                  "message": "A new course is available"
                }
                """;

        doThrow(new IllegalStateException("Processing failed"))
                .when(processingService)
                .process(argThat(
                        (NotificationEvent event) ->
                                "scrum171-test-003".equals(event.eventId())
                ));

        assertThrows(
                IllegalStateException.class,
                () -> consumer.consume(payload)
        );
    }
}
