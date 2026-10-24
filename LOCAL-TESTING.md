\# Notification Service — Local Testing



\## Purpose



Run and demonstrate the Notification Service locally without requiring AWS SQS.



\## Start the service



From the project directory:



```powershell

mvn spring-boot:run

```



\## Health check



Request:



`GET http://localhost:8085/health`



Expected result: service status is `UP`.



\## Submit a valid notification



Request:



`POST http://localhost:8085/api/notifications/test`



Example JSON:



```json

{

&#x20; "eventId": "demo-001",

&#x20; "eventType": "COURSE\_CREATED",

&#x20; "recipientId": "student-123",

&#x20; "subject": "New course",

&#x20; "message": "A new course is available"

}

```



Expected result: `ACCEPTED`, with the message `Notification processed locally`.



\## Invalid input test



Submit an event with empty `eventType` and `message`.



Observed result: HTTP 400 Bad Request.



\## Automated tests



Latest recorded result: 14 tests, 0 failures, 0 errors.



\## Current limitations



\- Notification output is console-only; real email delivery has not been verified.

\- SQS consumer tests use a mocked SQS client.

\- Real AWS SQS integration is deferred until the team is ready to configure the new AWS environment.

