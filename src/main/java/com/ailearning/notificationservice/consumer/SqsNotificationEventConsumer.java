package com.ailearning.notificationservice.consumer;

import com.ailearning.notificationservice.event.NotificationEvent;
import com.ailearning.notificationservice.service.NotificationProcessingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;

@Component
@ConditionalOnProperty(name = "notification.sqs.queue-url")
public class SqsNotificationEventConsumer {

    private static final Logger log =
            LoggerFactory.getLogger(SqsNotificationEventConsumer.class);

    private final SqsClient sqsClient;
    private final ObjectMapper objectMapper;
    private final NotificationProcessingService processingService;
    private final String queueUrl;

    public SqsNotificationEventConsumer(
            SqsClient sqsClient,
            ObjectMapper objectMapper,
            NotificationProcessingService processingService,
            @Value("${notification.sqs.queue-url}") String queueUrl) {
        this.sqsClient = sqsClient;
        this.objectMapper = objectMapper;
        this.processingService = processingService;
        this.queueUrl = queueUrl;
    }

    @Scheduled(fixedDelayString =
            "${notification.sqs.poll-interval-ms:5000}")
    public void pollQueue() {
        var response = sqsClient.receiveMessage(
                ReceiveMessageRequest.builder()
                        .queueUrl(queueUrl)
                        .maxNumberOfMessages(10)
                        .waitTimeSeconds(10)
                        .visibilityTimeout(30)
                        .build());

        for (Message message : response.messages()) {
            processMessage(message);
        }
    }

    private void processMessage(Message message) {
        try {
            NotificationEvent event = objectMapper.readValue(
                    message.body(), NotificationEvent.class);

            if (event == null || !event.hasMinimumRequiredFields()) {
                throw new IllegalArgumentException(
                        "eventType and message are required");
            }

            processingService.process(event);

            sqsClient.deleteMessage(
                    DeleteMessageRequest.builder()
                            .queueUrl(queueUrl)
                            .receiptHandle(message.receiptHandle())
                            .build());

            log.info("SQS notification processed. messageId={}, eventId={}",
                    message.messageId(), event.eventId());

        } catch (Exception ex) {
            log.error(
                    "SQS notification processing failed. messageId={}",
                    message.messageId(), ex);
        }
    }
}
