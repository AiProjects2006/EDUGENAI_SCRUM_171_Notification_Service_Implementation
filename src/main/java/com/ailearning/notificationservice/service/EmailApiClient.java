
package com.ailearning.notificationservice.service;

import com.ailearning.notificationservice.dto.EmailApiRequest;
import com.ailearning.notificationservice.dto.EmailApiResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class EmailApiClient {

    private final RestClient restClient;

    public EmailApiClient(
            RestClient.Builder builder,
            @Value("${email.api.base-url}") String baseUrl) {

        this.restClient = builder
                .baseUrl(baseUrl)
                .build();
    }

    public EmailApiResponse sendEmail(EmailApiRequest request) {

        return restClient.post()
                .uri("/api/email/send")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(EmailApiResponse.class);
    }
}
