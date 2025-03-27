package com.api;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * Main application class for the Spring Boot application.
 */
@SpringBootApplication
public class Application {
	/**
	 * Creates a {@link ModelMapper} bean.
	 *
	 * @return a new instance of {@link ModelMapper}
	 */
	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}

	/**
	 * Main method to run the Spring Boot application.
	 *
	 * @param args command line arguments
	 */
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
