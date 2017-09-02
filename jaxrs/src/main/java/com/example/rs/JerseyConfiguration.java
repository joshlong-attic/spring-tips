package com.example.rs;

import com.example.rs.customers.CustomerRepository;
import com.example.rs.customers.CustomerResource;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JerseyConfiguration {

    @Configuration
    public static class JerseyResourceConfiguration extends ResourceConfig
            implements InitializingBean {

        private final GenericExceptionMapper exceptionMapper;
        private final CustomerResource customerResource;

        public JerseyResourceConfiguration(
                GenericExceptionMapper exceptionMapper,
                CustomerResource customerResource) {
            this.exceptionMapper = exceptionMapper;
            this.customerResource = customerResource;
        }

        @Override
        public void afterPropertiesSet() throws Exception {
            this.register(this.exceptionMapper);
            this.register(this.customerResource);
        }
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
