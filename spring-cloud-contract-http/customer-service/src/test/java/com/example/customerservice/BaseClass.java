package com.example.customerservice;

import com.jayway.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;

/**
 * @author <a href="mailto:josh@joshlong.com">Josh Long</a>
 */
@SpringBootTest(classes = CustomerServiceApplication.class)
@RunWith(SpringRunner.class)
public class BaseClass {

    @Autowired
    private CustomerRestController customerRestController;

    @MockBean
    private CustomerRepository customerRepository;

    @Before
    public void before() {
        RestAssuredMockMvc.standaloneSetup(this.customerRestController);

        Mockito.when(this.customerRepository.findAll())
                .thenReturn(Arrays.asList(new Customer(1L, "Foo"), new Customer(2L, "Bar")));

    }
}
