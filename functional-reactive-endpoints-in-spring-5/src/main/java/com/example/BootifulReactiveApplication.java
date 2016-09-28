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
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.http.server.reactive.ReactorHttpHandlerAdapter;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.Request;
import org.springframework.web.reactive.function.Response;
import org.springframework.web.reactive.function.RouterFunction;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.ipc.netty.http.HttpServer;

import java.util.Optional;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import static org.springframework.web.reactive.function.RequestPredicates.GET;
import static org.springframework.web.reactive.function.Response.*;
import static org.springframework.web.reactive.function.RouterFunctions.route;
import static org.springframework.web.reactive.function.RouterFunctions.toHttpHandler;

@SpringBootApplication
public class BootifulReactiveApplication {

    @Bean
    RouterFunction<?> router(PersonHandler handler) {
        return route(GET("/persons"), handler::all).and(route(GET("/persons/{id}"), handler::byId));
    }

    @Bean
    HttpServer server(RouterFunction<?> router) {
        HttpHandler handler = toHttpHandler(router);
        HttpServer httpServer = HttpServer.create(8080);
        httpServer.start(new ReactorHttpHandlerAdapter(handler));
        return httpServer;
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

    Response<Flux<Person>> all(Request request) {
        Flux<Person> flux = Flux.fromStream(personRepository.all());
        return ok().body(BodyInserters.fromPublisher(flux, Person.class));
    }

    Response<Mono<Person>> byId(Request request) {
        Optional<String> optional = request.pathVariable("id");
        return optional.map(personRepository::findById)
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