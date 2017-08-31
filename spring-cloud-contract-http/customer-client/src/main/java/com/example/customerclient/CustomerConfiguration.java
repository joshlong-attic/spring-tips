package com.example.customerclient;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * @author <a href="mailto:josh@joshlong.com">Josh Long</a>
 */
@Configuration
public class CustomerConfiguration {

    @Bean
    CustomerClient client(RestTemplate restTemplate) {
        return new CustomerClient(restTemplate);
    }

    @Bean
    RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
