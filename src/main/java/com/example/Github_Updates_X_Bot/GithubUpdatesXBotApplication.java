package com.example.Github_Updates_X_Bot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class GithubUpdatesXBotApplication {

	public static void main(String[] args) {
		SpringApplication.run(GithubUpdatesXBotApplication.class, args);
	}

}
