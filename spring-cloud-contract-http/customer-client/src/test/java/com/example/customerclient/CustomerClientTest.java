package com.example.customerclient;

import org.assertj.core.api.BDDAssertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collection;

@SpringBootTest(classes = CustomerClientApplication.class)
@RunWith(SpringRunner.class)
@AutoConfigureStubRunner(ids = "com.example:customer-service:+:8081", workOffline = true)
public class CustomerClientTest {

    @Autowired
    private CustomerClient client;

    @Test
    public void shouldReturnAllCustomers() {
        Collection<Customer> allCustomers = this.client.getAllCustomers();
        BDDAssertions.then(allCustomers).size().isEqualTo(2);
    }
}
