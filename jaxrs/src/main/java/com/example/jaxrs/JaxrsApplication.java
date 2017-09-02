package com.example.jaxrs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.ws.rs.ApplicationPath;
import java.util.stream.Stream;

@SpringBootApplication
@ApplicationPath("/root")
public class JaxrsApplication {
    JaxrsApplication(CustomerRepository customerRepository) {
        Stream.of("A", "B", "C").forEach(c -> customerRepository.save(new Customer(null, c)));
    }

    public static void main(String[] args) {
        SpringApplication.run(JaxrsApplication.class, args);
    }
}

