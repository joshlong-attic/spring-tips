package com.example.customerclient;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Collection;

/**
 * @author <a href="mailto:josh@joshlong.com">Josh Long</a>
 */

public class CustomerClient {

    private final RestTemplate restTemplate;

    public CustomerClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Collection<Customer> getAllCustomers() {
        ParameterizedTypeReference<Collection<Customer>> type =
                new ParameterizedTypeReference<Collection<Customer>>() {
                };
        String url = "http://localhost:8081/customers";
        return this.restTemplate.exchange(
                url,
                HttpMethod.GET,
                RequestEntity.get(URI.create(url)).accept(MediaType.APPLICATION_JSON).build(),
                type
        )
                .getBody();
    }

}


