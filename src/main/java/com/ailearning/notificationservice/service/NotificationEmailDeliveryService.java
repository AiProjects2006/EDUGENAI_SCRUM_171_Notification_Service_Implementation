
package com.ailearning.notificationservice.service;

import com.ailearning.notificationservice.dto.EmailApiRequest;
import com.ailearning.notificationservice.dto.EmailApiResponse;

import org.springframework.stereotype.Service;

@Service
public class NotificationEmailDeliveryService {

    private final EmailApiClient emailApiClient;

    public NotificationEmailDeliveryService(
            EmailApiClient emailApiClient) {

        this.emailApiClient = emailApiClient;
    }

    public EmailApiResponse sendNotificationEmail(
            String recipientEmail,
            String subject,
            String message) {

        EmailApiRequest request = new EmailApiRequest(
                recipientEmail,
                subject,
                message
        );

        EmailApiResponse response =
                emailApiClient.sendEmail(request);

        if (response == null ||
                !"SUCCESS".equalsIgnoreCase(response.status())) {

            throw new IllegalStateException(
                    "Email API did not confirm successful delivery"
            );
        }

        return response;
    }
}
