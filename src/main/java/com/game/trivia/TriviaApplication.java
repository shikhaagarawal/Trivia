package com.game.trivia;

import com.game.trivia.config.TriviaConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
public class TriviaApplication {

	//TODO log4j

	public static void main(String[] args) {
		SpringApplication.run(TriviaApplication.class, args);
	}

}
