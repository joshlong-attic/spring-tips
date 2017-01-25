package com.example;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.annotation.Id;
import org.springframework.data.couchbase.core.mapping.Document;
import org.springframework.data.couchbase.repository.CouchbaseRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

@SpringBootApplication
public class CouchbaseDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(CouchbaseDemoApplication.class, args);
	}
}

interface MovieRepository extends CouchbaseRepository<Movie, String> {

	CompletableFuture<Movie> findByTitle(String title);
}

@Component
class SampleMovieCLR implements CommandLineRunner {

	private final MovieRepository movieRepository;

	public SampleMovieCLR(MovieRepository movieRepository) {
		this.movieRepository = movieRepository;
	}

	@Override
	public void run(String... args) throws Exception {
		String ozTitle = "The Wizard of Oz";
		Stream.of("Gone with the Wind", ozTitle, "One Flew over the Cuckoo's Nest", "Dr. Strangelove")
				.forEach(title -> this.movieRepository.save(new Movie(UUID.randomUUID().toString(), title)));
		this.movieRepository.findByTitle(ozTitle).thenAccept(System.out::println);
	}
}

@Document
@Data
@AllArgsConstructor
@NoArgsConstructor
class Movie {

	@Id
	private String id;
	private String title;
}