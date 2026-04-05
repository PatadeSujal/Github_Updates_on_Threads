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

    public String generatePost(String repoName, String repoDescription, String commitMessage, String commitUrl) {
        if (apiKey == null || apiKey.isEmpty() || apiKey.equals("YOUR_GEMINI_API_KEY")) {
            System.out.println("[MOCK] Gemini API Key missing! Returning basic string.");
            return "🚀 Update to " + repoName + "!\n\n📝 Changes: " + commitMessage;
        }

        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent?key=" + apiKey;

        String prompt = "You are an enthusiastic developer sharing a project update on Threads. " +
                "Write an engaging Threads post explaining this code commit.\n" +
                "Project Name: " + repoName + "\n" +
                "Project Context: " + (repoDescription != null && !repoDescription.isEmpty() ? repoDescription : "A cool software application") + "\n" +
                "Commit Message: " + commitMessage + "\n" +
                "Commit URL: " + (commitUrl != null ? commitUrl : "") + "\n\n" +
                "STRICT INSTRUCTIONS:\n" +
                "1. Briefly explain what the project is so readers have context.\n" +
                "2. Explain what the new commit actually means for the project.\n" +
                "3. You MUST include the exact Commit URL at the end of the post so people can click it.\n" +
                "4. Do NOT use any hashtags.\n" +
                "5. Keep the total post under 400 characters.\n" +
                "6. Do not wrap the response in quotation marks.";

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
