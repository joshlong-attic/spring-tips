package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@EnableDiscoveryClient
@RestController
public class ClientBApplication {

	@GetMapping("/")
	public String name() {
		return "b";
	}

	public static void main(String[] args) {
		SpringApplication.run(ClientBApplication.class, args);
	}
}
