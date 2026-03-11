package ru.practice.parser_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ParserServiceApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(ParserServiceApiApplication.class, args);
	}
}
