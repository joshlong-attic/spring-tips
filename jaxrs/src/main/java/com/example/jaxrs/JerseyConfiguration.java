package com.example.jaxrs;

import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * @author <a href="mailto:josh@joshlong.com">Josh Long</a>
 */
@Configuration
public class JerseyConfiguration extends ResourceConfig {

    @PostConstruct
    public void setUp() {
        register(CustomerResource.class);
    }
}
