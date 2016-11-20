package com.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.stream.Stream;

@SpringBootApplication
public class CfDataDemoApplication implements CommandLineRunner {
    @Override
    public void run(String... strings) throws Exception {

        Stream.of("Felix", "Garfield", "Tuppins")
                .forEach(name -> catRepository.save(new Cat(name)));
    }

    private final CatRepository catRepository;

    @Autowired
    public CfDataDemoApplication(CatRepository rr) {
        this.catRepository = rr;
    }

    public static void main(String[] args) {
        SpringApplication.run(CfDataDemoApplication.class, args);
    }
}

@Entity
class Cat {
    @Override
    public String toString() {
        return "Cat{" +
                "id=" + id +
                ", reservationName='" + reservationName + '\'' +
                '}';
    }

    @Id
    @GeneratedValue
    private Long id;

    Cat() {
    }

    private String reservationName;

    public Cat(String reservationName) {
        this.reservationName = reservationName;
    }

    public Long getId() {

        return id;
    }

    public String getReservationName() {
        return reservationName;
    }
}

