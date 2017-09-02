package com.example.jaxrs;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

/**
 * @author <a href="mailto:josh@joshlong.com">Josh Long</a>
 */
@Component
@Path("/customers")
@Produces(MediaType.APPLICATION_JSON_VALUE)
public class CustomerResource {

    private final CustomerRepository customerRepository;

    CustomerResource(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @GET
    @Path("/{id}")
    public Response byId(@PathParam("id") Long id) {
        return Response
                .ok(this.customerRepository.findById(id))
                .build();
    }

    @GET
    public Response all() {
        return Response.ok(this.customerRepository.findAll()).build();
    }
}
