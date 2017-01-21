package com.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

//http://docs.spring.io/spring-framework/docs/5.0.0.BUILD-SNAPSHOT/spring-framework-reference/html/web-reactive.html
@SpringBootApplication
public class BootifulReactiveApplication {

	@Bean
	RouterFunction<?> router(PersonHandler handler) {
		return route(GET("/persons"), handler::all).and(route(GET("/persons/{id}"), handler::byId));
	}

	public static void main(String[] args) {
		SpringApplication.run(BootifulReactiveApplication.class, args);
	}
}


@Component
class PersonHandler {

	private final PersonRepository personRepository;

	@Autowired
	public PersonHandler(PersonRepository personRepository) {
		this.personRepository = personRepository;
	}

	Mono<ServerResponse> all(ServerRequest request) {
		Flux<Person> flux = Flux.fromStream(personRepository.all());
		return ok().body(
				BodyInserters.fromPublisher(flux, Person.class));
	}

	Mono<ServerResponse> byId(ServerRequest request) {
		String id = request.pathVariable("id");
		return Optional.ofNullable(id).map(personRepository::findById)
				.map(Mono::fromFuture)
				.map(mono -> ok().body(BodyInserters.fromPublisher(mono, Person.class)))
				.orElseThrow(() -> new IllegalStateException("Oops!"));

	}
}

@Component
class SampleDataCLR implements CommandLineRunner {

	private final PersonRepository personRepository;

	@Autowired
	public SampleDataCLR(PersonRepository personRepository) {
		this.personRepository = personRepository;
	}

	@Override
	public void run(String... strings) throws Exception {

		personRepository.deleteAll();

		Stream.of("Stephane Maldini", "Arjen Poutsma", "Rossen Stoyanchev",
				"Sebastien Deleuze", "Josh Long").forEach(name ->
				personRepository.save(new Person(name, new Random().nextInt(100))));
		personRepository.findAll().forEach(System.out::println);
	}
}

interface PersonRepository extends MongoRepository<Person, String> {

	CompletableFuture<Person> findById(String id);

	@Query("{}")
	Stream<Person> all();
}

@Document
class Person {

	@Id
	private String id;

	private String name;

	private int age;

	@Override
	public String toString() {
		return "Person{" +
				"id='" + id + '\'' +
				", name='" + name + '\'' +
				", age=" + age +
				'}';
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public int getAge() {
		return age;
	}

	public Person() {
	}

	public Person(String name, int age) {
		this.name = name;
		this.age = age;
	}
}