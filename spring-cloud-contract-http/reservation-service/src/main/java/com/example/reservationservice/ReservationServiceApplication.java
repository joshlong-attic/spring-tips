package com.example.reservationservice;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Collection;


@SpringBootApplication
public class ReservationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReservationServiceApplication.class, args);
    }
}

@RestController
class ReservationRestController {

    @GetMapping("/reservations")
    Collection<Reservation> reservations() {
        return Arrays.asList(new Reservation("Josh"), new Reservation("Marcin"));
    }
}

@Data
@NoArgsConstructor
@AllArgsConstructor
class Reservation {
    private String reservationName;
}