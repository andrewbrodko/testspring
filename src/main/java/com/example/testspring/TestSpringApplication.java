package com.example.testspring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class TestSpringApplication {
	public static void main(String[] args) {
		SpringApplication.run(TestSpringApplication.class, args);
	}
}