package com.example;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;

import static org.springframework.web.reactive.function.client.ClientRequest.GET;


@SpringBootApplication
public class ReactiveClientApplication {

	private Log log = LogFactory.getLog(getClass());

	@Bean
	CommandLineRunner demo(WebClient webClient) {
		return args -> {

			// dependent calls
			// TODO: my kingdom for a logger
			webClient.exchange(GET("http://localhost:8080/persons/588409d868dde8065d47edfa").build())
					.flatMap(clientResponse -> clientResponse.bodyToMono(Person.class))
					.subscribe(log::info);

			webClient.exchange(GET("http://localhost:8080/persons").build())
					.flatMap(clientResponse -> clientResponse.bodyToFlux(Person.class))
					.subscribe(log::info);

			webClient.exchange(GET("http://localhost:8080/sse/person").build())
					.flatMap(cr -> cr.bodyToFlux(Person.class))
					.log()
					.subscribe(log::info);
		};
	}

	@Bean
	WebClient webClient() {
		return WebClient.create(new ReactorClientHttpConnector());
	}

	public static void main(String[] args) {
		SpringApplication.run(ReactiveClientApplication.class, args);
	}
}


@Data
@AllArgsConstructor
@NoArgsConstructor
class Person {
	private String id;
	private String name;
	private int age;
}
