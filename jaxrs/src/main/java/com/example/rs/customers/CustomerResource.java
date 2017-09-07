package com.example.rs.customers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

/**
 * @author <a href="mailto:josh@joshlong.com">Josh Long</a>
 */
@Slf4j
@Path("/customers")
@Produces(MediaType.APPLICATION_JSON_VALUE)
public class CustomerResource {

    private final CustomerRepository customerRepository;

    public CustomerResource(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @GET
    @Path("/{id}")
    public Response byId(@PathParam("id") Long id,
                         @Context SecurityContext ctx) throws CustomerNotFoundException {
        log.info(String.format("%s was here.", ctx.getUserPrincipal().getName()));
        Customer byId = this.customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(id));
        return Response.ok(byId).build();
    }

    @GET
    public Response all() throws Exception {
        return Response.ok(this.customerRepository.findAll()).build();
    }
}
