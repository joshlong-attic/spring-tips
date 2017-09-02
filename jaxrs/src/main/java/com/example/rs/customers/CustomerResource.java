package com.example.rs.customers;

import org.springframework.http.MediaType;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

/**
 * @author <a href="mailto:josh@joshlong.com">Josh Long</a>
 */
@Path("/customers")
@Produces(MediaType.APPLICATION_JSON_VALUE)
public class CustomerResource {

    private final CustomerRepository customerRepository;

    public CustomerResource(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @GET
    @Path("/{id}")
    public Response byId(@PathParam("id") Long id) throws CustomerNotFoundException {
        Customer byId = this.customerRepository
                .findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(id));
        return Response
                .ok(byId)
                .build();
    }

    @GET
    public Response all() throws Exception {
        return Response
                .ok(this.customerRepository.findAll())
                .build();
    }
}
