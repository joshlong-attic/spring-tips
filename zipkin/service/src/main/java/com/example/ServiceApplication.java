package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SpringBootApplication
public class ServiceApplication {

	@GetMapping("/hi")
	String hi() {
		return "Hello";
	}

	public static void main(String[] args) {
		SpringApplication.run(ServiceApplication.class, args);
	}
}
