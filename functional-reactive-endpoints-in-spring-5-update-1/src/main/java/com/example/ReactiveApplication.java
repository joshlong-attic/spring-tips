package com.example;

import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.reactivestreams.Publisher;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.config.EnableWebReactive;
import org.springframework.web.reactive.config.WebReactiveConfigurer;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import org.springframework.web.reactive.socket.server.WebSocketService;
import org.springframework.web.reactive.socket.server.support.HandshakeWebSocketService;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;
import org.springframework.web.reactive.socket.server.upgrade.ReactorNettyRequestUpgradeStrategy;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.ReplayProcessor;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.stream.IntStream;

import static com.example.ReactiveApplication.randomPerson;

interface PersonRepository extends ReactiveCrudRepository<Person, String> {
	Mono<Person> findById(String id);
}

@SpringBootApplication(exclude = {MongoDataAutoConfiguration.class})
public class ReactiveApplication {

	static Person randomPerson() {
		String[] names = {"Stephane Maldini", "Arjen Poutsma",
				"Mark Heckler", "Juergen Hoeller", "Phil Webb", "Dr. Syer", "Philz Coffee",
				"Madhura Bhave", "Rossen Stoyanchev", "Sebastien Deleuze"};
		int nameIndx = new Random().nextInt(names.length);
		String name = names[nameIndx];
		int age = new Random().nextInt(100);
		return new Person(name, age);
	}

	public static void main(String[] args) {
		SpringApplication.run(ReactiveApplication.class, args);
	}

/*
	// todo
	@Bean
	RouterFunction<?> router(PersonHandler handler) {
		return
				route(GET("/persons"), handler::all)
						.andRoute(GET("/persons/{id}"), handler::byId);
	}
	*/

	@Bean
	CommandLineRunner init(PersonRepository personRepository) {
		return args -> {
			personRepository.deleteAll().block();
			Publisher<Person> personPublisher = Flux.fromStream(IntStream.range(0, 10).mapToObj(i -> randomPerson()));
			personRepository.save(personPublisher)
					.doOnComplete(() -> personRepository.findAll().subscribe(System.out::println))
					.subscribe();
		};
	}
}

@RestController
class PersonRestController {

	private final PersonRepository personRepository;

	public PersonRestController(PersonRepository personRepository) {
		this.personRepository = personRepository;
	}

	@GetMapping("/persons")
	Flux<Person> all() {
		return personRepository.findAll();
	}

	@GetMapping("/persons/{id}")
	Mono<Person> byId(@PathVariable String id) {
		return personRepository.findById(id);
	}
}

@RestController
class SseController {

	private ReplayProcessor<ServerSentEvent<String>> replayProcessor = ReplayProcessor.create();

	@GetMapping("/sse/string")
	Flux<String> string() {
		return Flux
				.interval(Duration.ofSeconds(1))
				.map(l -> "foo " + l);
	}

	@GetMapping("/sse/person")
	Flux<ServerSentEvent<Person>> person() {
		return Flux
				.interval(Duration.ofSeconds(1))
				.map(l -> randomPerson())
				.map(person -> ServerSentEvent.builder(person).build());
	}

	@GetMapping("/sse/event")
	Flux<ServerSentEvent<String>> event() {
		return Flux
				.interval(Duration.ofSeconds(1))
				.map(l -> ServerSentEvent
						.builder("foo\nbar")
						.comment("bar\nbaz")
						.id(Long.toString(l))
						.build());
	}

	@PostMapping("/sse/receive/{val}")
	public void receive(@PathVariable("val") String s) {
		replayProcessor.onNext(ServerSentEvent.builder(s).build());
	}

	@GetMapping("/sse/send")
	public Flux<ServerSentEvent<String>> send() {
		return replayProcessor.log("playground");
	}

}

@Configuration
@EnableWebReactive
class WebReactiveConfiguration implements WebReactiveConfigurer {

	@Bean
	public HandlerMapping handlerMapping() {

		Map<String, WebSocketHandler> map = new HashMap<>();
		map.put("/websocket/echo", new EchoWebSocketHandler());

		SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();
		mapping.setUrlMap(map);
		return mapping;
	}

	@Bean
	public WebSocketHandlerAdapter handlerAdapter() {
		return new WebSocketHandlerAdapter(webSocketService());
	}

	@Bean
	public WebSocketService webSocketService() {
		return new HandshakeWebSocketService(new ReactorNettyRequestUpgradeStrategy());
	}

	private static class EchoWebSocketHandler implements WebSocketHandler {

		@Override
		public Mono<Void> handle(WebSocketSession session) {
			// Use retain() for Reactor Netty
			return session.send(session.receive().doOnNext(WebSocketMessage::retain).delay(Duration.ofSeconds(2)));
		}
	}
}

@Configuration
@EnableReactiveMongoRepositories
class ReactiveMongoConfiguration extends AbstractReactiveMongoConfiguration {

	@Override
	protected String getDatabaseName() {
		return "person";
	}

	@Override
	public MongoClient mongoClient() {
		return MongoClients.create();
	}
}

@Document
@Data
@NoArgsConstructor
class Person {

	@Id
	private String id;
	private String name;
	private int age;

	public Person(String name, int age) {
		this.name = name;
		this.age = age;
	}
}