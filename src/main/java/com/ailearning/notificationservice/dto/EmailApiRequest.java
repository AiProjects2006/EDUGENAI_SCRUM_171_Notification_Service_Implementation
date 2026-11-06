
package com.ailearning.notificationservice.dto;

public record EmailApiRequest(

        String to,

        String subject,

        String message

) {
}
