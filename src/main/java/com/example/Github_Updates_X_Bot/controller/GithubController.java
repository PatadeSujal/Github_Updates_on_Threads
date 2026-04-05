package com.example.Github_Updates_X_Bot.controller;

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

    @Autowired
    private ThreadsService threadsService;

    @PostMapping("/push")
    public ResponseEntity<Void> handlePush(@RequestBody Map<String, Object> payload) {
        // Extract info
        Map<String, Object> repository = (Map<String, Object>) payload.get("repository");
        String repoName = (String) repository.get("name");

        List<Map<String, Object>> commits = (List<Map<String, Object>>) payload.get("commits");
        if (commits != null && !commits.isEmpty()) {
            String message = (String) commits.get(0).get("message");

            // Trigger the thread post
            threadsService.postThread("🚀 New Code Pushed to " + repoName + ": " + message);
        }

        return ResponseEntity.ok().build();
    }
}
