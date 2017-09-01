package com.example.jaxrs;

import lombok.Data;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

@SpringBootApplication
public class JaxrsApplication {


    public static void main(String[] args) {
        SpringApplication.run(JaxrsApplication.class, args);
    }
}

interface CustomerRepository extends JpaRepository<Customer, Long> {
}

@Entity
@Data
class Customer {

    @Id
    @GeneratedValue
    private Long id;

    private String name;
}

@Component
@Path("/customers")
class CustomerResource {

    private final CustomerRepository customerRepository;

    CustomerResource(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @GET
    @Path("/{id}")
    public Response byId(@PathParam("id") Long id) {
        return Response.ok(this.customerRepository.findById(id)).build();
    }
}