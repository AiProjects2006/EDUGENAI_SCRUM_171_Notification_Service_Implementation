package com.ailearning.notificationservice.consumer;

import com.ailearning.notificationservice.event.NotificationEvent;
import com.ailearning.notificationservice.service.NotificationProcessingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageResponse;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SqsNotificationEventConsumerTest {

    private SqsClient sqsClient;
    private NotificationProcessingService processingService;
    private SqsNotificationEventConsumer consumer;

    @BeforeEach
    void setUp() {
        sqsClient = mock(SqsClient.class);
        processingService = mock(NotificationProcessingService.class);
        consumer = new SqsNotificationEventConsumer(
                sqsClient,
                new ObjectMapper().findAndRegisterModules(),
                processingService,
                "https://example.com/test-queue"
        );
    }

    @Test
    void successfulProcessingDeletesMessage() {
        Message message = Message.builder()
                .messageId("message-001")
                .receiptHandle("receipt-001")
                .body("""
                        {
                          "eventId": "sqs-test-001",
                          "eventType": "COURSE_CREATED",
                          "recipientId": "student-123",
                          "subject": "New course",
                          "message": "A new course is available",
                          "data": {}
                        }
                        """)
                .build();

        when(sqsClient.receiveMessage(any(ReceiveMessageRequest.class)))
                .thenReturn(ReceiveMessageResponse.builder()
                        .messages(message).build());

        consumer.pollQueue();

        verify(processingService).process(any(NotificationEvent.class));
        verify(sqsClient).deleteMessage(any(DeleteMessageRequest.class));
    }

    @Test
    void failedProcessingDoesNotDeleteMessage() {
        Message message = Message.builder()
                .messageId("message-002")
                .receiptHandle("receipt-002")
                .body("""
                        {
                          "eventId": "sqs-test-002",
                          "eventType": "COURSE_CREATED",
                          "recipientId": "student-123",
                          "message": "A new course is available"
                        }
                        """)
                .build();

        when(sqsClient.receiveMessage(any(ReceiveMessageRequest.class)))
                .thenReturn(ReceiveMessageResponse.builder()
                        .messages(message).build());

        doThrow(new IllegalStateException("Delivery failed"))
                .when(processingService)
                .process(any(NotificationEvent.class));

        consumer.pollQueue();

        verify(sqsClient, never())
                .deleteMessage(any(DeleteMessageRequest.class));
    }
}
