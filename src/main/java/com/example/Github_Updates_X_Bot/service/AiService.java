package com.example.Github_Updates_X_Bot.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AiService {

    @Value("${gemini.api.key:YOUR_GEMINI_API_KEY}")
    private String apiKey;

    private final RestTemplate restTemplate;

    public AiService() {
        this.restTemplate = new RestTemplate();
    }

    public String generatePost(String repoName, String repoDescription, String commitMessage) {
        if (apiKey == null || apiKey.isEmpty() || apiKey.equals("YOUR_GEMINI_API_KEY")) {
            System.out.println("[MOCK] Gemini API Key missing! Returning basic string.");
            return "🚀 Update to " + repoName + "!\n\n📝 Changes: " + commitMessage;
        }

        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-pro:generateContent?key=" + apiKey;

        String prompt = "You are a professional social media manager for a developer. " +
                "Write a short, engaging Threads post about a new code commit to a GitHub repository.\n" +
                "Repository Name: " + repoName + "\n" +
                "Repository Description: " + (repoDescription != null ? repoDescription : "N/A") + "\n" +
                "Commit Message: " + commitMessage + "\n" +
                "Instructions: Keep it under 250 characters. Be casual and enthusiastic. Do NOT use any hashtags. Do not wrap the response in quotes.";

        Map<String, Object> textPart = new HashMap<>();
        textPart.put("text", prompt);

        Map<String, Object> content = new HashMap<>();
        content.put("parts", Collections.singletonList(textPart));

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("contents", Collections.singletonList(content));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.postForObject(url, request, Map.class);
            if (response != null && response.containsKey("candidates")) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.get("candidates");
                if (!candidates.isEmpty()) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> candidateContent = (Map<String, Object>) candidates.get(0).get("content");
                    @SuppressWarnings("unchecked")
                    List<Map<String, Object>> parts = (List<Map<String, Object>>) candidateContent.get("parts");
                    String text = (String) parts.get(0).get("text");
                    return text != null ? text.trim() : null;
                }
            }
        } catch (Exception e) {
            System.err.println("Error calling Gemini API: " + e.getMessage());
        }

        // Fallback if API fails
        return "🚀 Update to " + repoName + "!\n\n📝 Changes: " + commitMessage;
    }
}
