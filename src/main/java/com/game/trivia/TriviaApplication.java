package com.game.trivia;

import com.game.trivia.service.GameInstanceService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class TriviaApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(TriviaApplication.class, args);
    }

    @Bean
    public GameInstanceService gameInstanceService() {
        return new GameInstanceService();
    }
}
