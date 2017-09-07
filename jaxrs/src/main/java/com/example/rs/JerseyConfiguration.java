package com.example.rs;

import com.example.rs.customers.CustomerRepository;
import com.example.rs.customers.CustomerResource;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author <a href="mailto:josh@joshlong.com">Josh Long</a>
 */
@Configuration
public class JerseyConfiguration {

    @Bean
    ResourceConfig resourceConfig(GenericExceptionMapper exceptionMapper,
                                  CustomerResource customerResource) {
        ResourceConfig rc = new ResourceConfig();
        rc.register(exceptionMapper);
        rc.register(customerResource);
        return rc;
    }

    @Bean
    GenericExceptionMapper exceptionMapper() {
        return new GenericExceptionMapper();
    }

    @Bean
    CustomerResource customerResource(CustomerRepository customerRepository) {
        return new CustomerResource(customerRepository);
    }
}
