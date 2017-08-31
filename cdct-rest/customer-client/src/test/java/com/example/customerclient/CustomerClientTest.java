package com.example.customerclient;

import org.assertj.core.api.BDDAssertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collection;

/**
 * @author <a href="mailto:josh@joshlong.com">Josh Long</a>
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CustomerClientApplication.class)
@AutoConfigureStubRunner(ids = "com.example:customer-service:+:8081", workOffline = true)
public class CustomerClientTest {

    /*
    @Autowired
    private ObjectMapper objectMapper;
    */

    @Autowired
    private CustomerClient client;

    @Test
    public void clientShouldReturnAllCustomers() throws Exception {

        /*
        WireMock.stubFor(WireMock.get(WireMock.urlEqualTo("/customers"))
                .willReturn(
                        WireMock.aResponse()
                                .withStatus(200)
                                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE)
                                .withBody(jsonForCustomer(new Customer(1L, "Jane"),
                                        new Customer(2L, "Bob")))));
        */

        Collection<Customer> customers = this.client.getAllCustomers();
        BDDAssertions.then(customers).size().isEqualTo(2);
        BDDAssertions.then(customers.iterator().next().getId()).isEqualTo(1L);
        BDDAssertions.then(customers.iterator().next().getName()).isEqualTo("Jane");

    }
    /*
    private String jsonForCustomer(Customer... customers) throws Exception {
        List<Customer> customerList = Arrays.asList(customers);
        return this.objectMapper.writeValueAsString(customerList);
    }
    */
}