package service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.time.Duration;
import java.util.Date;
import java.util.stream.Stream;

@SpringBootApplication
@RestController
public class ReactiveServiceApplication {

	@GetMapping("/events/{id}")
	Mono<Event> eventById(@PathVariable long id) {
		return Mono.just(new Event(id, new Date()));
	}

	@GetMapping(value = "/events", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	Flux<Event> events() {
		return Flux.zip(Flux.interval(Duration.ofSeconds(1)),
				Flux.fromStream(Stream.generate(() -> new Event(System.currentTimeMillis(), new Date()))))
				.map(Tuple2::getT2);
	}

	public static void main(String[] args) {
		SpringApplication.run(ReactiveServiceApplication.class, args);
	}
}

