package com.example.Github_Updates_X_Bot.controller;

import com.example.Github_Updates_X_Bot.service.AiService;
import com.example.Github_Updates_X_Bot.service.ThreadsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/webhooks")
public class GithubController {

    private final ThreadsService threadsService;
    private final AiService aiService;

    @Autowired
    public GithubController(ThreadsService threadsService, AiService aiService) {
        this.threadsService = threadsService;
        this.aiService = aiService;
    }

    @PostMapping("/push")
    public ResponseEntity<Void> handlePush(@RequestBody Map<String, Object> payload) {
        // Extract info
        Map<String, Object> repository = (Map<String, Object>) payload.get("repository");
        String repoName = (String) repository.get("name");
        String repoDescription = (String) repository.get("description");

        List<Map<String, Object>> commits = (List<Map<String, Object>>) payload.get("commits");
        if (commits != null && !commits.isEmpty()) {
            String message = (String) commits.get(0).get("message");

            // Ignore commits that do not contain the trigger hashtag
            if (message == null || !message.toLowerCase().contains("#threads")) {
                System.out.println("Commit ignored: Missing #Threads hashtag.");
                return ResponseEntity.ok().build();
            }

            // Remove the hashtag from the text so it looks cleaner on Threads
            String cleanMessage = message.replaceAll("(?i)#threads", "").trim();

            String commitUrl = (String) commits.get(0).get("url");

            // Use the AI Service to generate a dynamic post
            String aiGeneratedPost = aiService.generatePost(repoName, repoDescription, cleanMessage, commitUrl);

            // Trigger the thread post
            threadsService.postThread(aiGeneratedPost);
        }

        return ResponseEntity.ok().build();
    }
}
