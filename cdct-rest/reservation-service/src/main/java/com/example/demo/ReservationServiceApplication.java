package com.example.demo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

@SpringBootApplication
public class ReservationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReservationServiceApplication.class, args);
    }
}

@NoArgsConstructor
@AllArgsConstructor
@Data
class Reservation {
    private String reservationName;
}

@RestController
class ReservationRestController {

    @GetMapping("/reservations/{id}")
    Reservation byId(@PathVariable Long id) {
        return new Reservation("Leroy");
    }

    @GetMapping("/reservations")
    Collection<Reservation> reservations() {
        return Arrays.asList(
                new Reservation("Marcin"),
                new Reservation("Bob"));
    }
}