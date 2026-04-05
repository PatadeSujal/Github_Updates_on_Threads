package com.example.Github_Updates_X_Bot.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class ThreadsService {

    @Value("${threads.user.id}")
    private String userId;

    @Value("${threads.access.token}")
    private String accessToken;

    private final RestTemplate restTemplate = new RestTemplate();

    public void postThread(String message) {
        if (userId == null || userId.isEmpty() || userId.equals("YOUR_THREADS_USER_ID") ||
            accessToken == null || accessToken.isEmpty() || accessToken.equals("YOUR_THREADS_ACCESS_TOKEN")) {
            System.err.println("[MOCK] Threads tokens missing. Would have posted: " + message);
            return;
        }

        System.out.println("Posting to Threads: " + message);
        try {
            // STEP 1: Create media container
            String createUrl = "https://graph.threads.net/v1.0/" + userId + "/threads";
            
            Map<String, String> createParams = Map.of(
                "text", message,
                "media_type", "TEXT",
                "access_token", accessToken
            );

            ResponseEntity<Map> createResponse = restTemplate.postForEntity(createUrl, createParams, Map.class);
            if (!createResponse.getStatusCode().is2xxSuccessful() || createResponse.getBody() == null) {
                System.err.println("Failed to create container: " + createResponse.getStatusCode());
                return;
            }

            String creationId = (String) createResponse.getBody().get("id");
            System.out.println("Threads container created with ID: " + creationId);

            // STEP 2: Publish media container
            String publishUrl = "https://graph.threads.net/v1.0/" + userId + "/threads_publish";
            
            Map<String, String> publishParams = Map.of(
                "creation_id", creationId,
                "access_token", accessToken
            );

            ResponseEntity<String> publishResponse = restTemplate.postForEntity(publishUrl, publishParams, String.class);
            if (publishResponse.getStatusCode().is2xxSuccessful()) {
                System.out.println("Successfully published thread!");
            } else {
                System.err.println("Failed to publish thread: " + publishResponse.getStatusCode());
            }

        } catch (Exception e) {
            System.err.println("Exception while posting to Threads API: " + e.getMessage());
        }
    }
}
