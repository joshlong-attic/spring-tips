package com.example.customerservice;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@SpringBootApplication
public class CustomerServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CustomerServiceApplication.class, args);
    }
}

interface CustomerRepository extends JpaRepository<Customer, Long> {
}

@RestController
class CustomerRestController {

    private final CustomerRepository customerRepository;

    CustomerRestController(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @GetMapping("/customers")
    public Collection<Customer> customers() {
        return this.customerRepository.findAll();
    }
}

@Component
class SampleDataInitializer implements ApplicationRunner {

    private final CustomerRepository customerRepository;

    SampleDataInitializer(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        List<Customer> customers = Arrays.asList(
                new Customer(1L, "Foo"), new Customer(2L, "Bar"));
        customerRepository.save(customers);
    }
}

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
class Customer {
    @Id
    @GeneratedValue
    private Long id;
    private String name;
}