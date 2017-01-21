package com.example;

import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.reactivestreams.Publisher;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.Random;
import java.util.stream.Stream;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@SpringBootApplication(exclude = MongoDataAutoConfiguration.class)
@EnableReactiveMongoRepositories
public class ReactiveApplication {

	private Log log = LogFactory.getLog(getClass());

	@Bean
	CommandLineRunner init(PersonRepository personRepository) {
		return args -> {
			personRepository.deleteAll().block();
			Publisher<Person> p = Flux.fromStream(Stream.of(
					"Stephane Maldini", "Arjen Poutsma", "Rossen Stoyanchev", "Sebastien Deleuze", "Josh Long")
					.map(name -> new Person(name, new Random().nextInt(100))));
			personRepository.save(p).subscribe(log::info);
		};
	}

	@Bean
	RouterFunction<?> router(PersonHandler handler) {
		return route(GET("/persons"), handler::all).and(route(GET("/persons/{id}"), handler::byId));
	}

	public static void main(String[] args) {
		SpringApplication.run(ReactiveApplication.class, args);
	}
}

@Configuration
@EnableReactiveMongoRepositories
class ReactiveAppConfig extends AbstractReactiveMongoConfiguration {

	@Override
	protected String getDatabaseName() {
		return "person";
	}

	@Override
	public MongoClient mongoClient() {
		return MongoClients.create();
	}
}

@Component
class PersonHandler {

	private final PersonRepository personRepository;

	public PersonHandler(PersonRepository personRepository) {
		this.personRepository = personRepository;
	}

	Mono<ServerResponse> all(ServerRequest request) {
		Flux<Person> flux = personRepository.findAll();
		return ok().body(BodyInserters.fromPublisher(flux, Person.class));
	}

	Mono<ServerResponse> byId(ServerRequest request) {
		String id = request.pathVariable("id");
		return Optional.ofNullable(id).map(personRepository::findById)
				.map(mono -> ok().body(BodyInserters.fromPublisher(mono, Person.class)))
				.orElseThrow(() -> new IllegalStateException("Oops!"));
	}
}


interface PersonRepository extends ReactiveCrudRepository<Person, String> {
	Mono<Person> findById(String id);
}

