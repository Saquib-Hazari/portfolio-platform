package com.nickhazari.portfolio;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.nickhazari.portfolio.repositories.UserRepository;

@SpringBootApplication(scanBasePackages = "com.nickhazari")

public class BackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}

	@Bean
	CommandLineRunner run(UserRepository userRepository) {
		return args -> {
			System.out.println("All users in the database:");
			userRepository.findAll().forEach(System.out::println);
		};
	}
}
