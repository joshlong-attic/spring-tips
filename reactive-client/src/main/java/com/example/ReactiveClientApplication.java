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
import reactor.core.publisher.Flux;
import reactor.util.function.Tuple2;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Random;
import java.util.stream.Stream;

import static org.springframework.web.reactive.function.client.ClientRequest.GET;


class MatchMaker {

	public MatchMaker() {
		Flux<Person> men = this.men();
		Flux<Person> women = this.women();
		Flux<Tuple2<Person, Person>> zip = Flux.zip(women, men).delay(Duration.of(1, ChronoUnit.SECONDS));
		zip.subscribe(tpl -> System.out.println(tpl.getT1().getName() + " and " + tpl.getT2().getName() + " sitting in a tree.."));
	}

	private Person randomPerson(String names[]) {
		int nameIndx = new Random().nextInt(names.length);
		String name = names[nameIndx];
		int age = new Random().nextInt(100);
		return new Person(name, age);
	}

	private Person randomWomen() {
		String[] names = {"Jane", "Janet", "Joan", "Linda", "Laura", "Marie", "Maria", "Penelope", "Pippi", "Chunhua"};
		return randomPerson(names);
	}

	private Person randomMen() {
		String[] names = {"Bob", "Joe", "Ling", "Jaime", "Jesus", "Georg", "Danny", "Hans"};
		return randomPerson(names);
	}

	private Flux<Person> women() {
		return Flux.fromStream(Stream.generate(this::randomWomen));
	}

	private Flux<Person> men() {
		return Flux.fromStream(Stream.generate(this::randomMen));
	}


}



@SpringBootApplication
public class ReactiveClientApplication {

	private Log log = LogFactory.getLog(getClass());


	//@Bean
	CommandLineRunner demo(WebClient webClient) {
		return args -> {

			// dependent calls
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

	Person(String name, int a) {
		this.age = a;
		this.name = name;
	}
}
