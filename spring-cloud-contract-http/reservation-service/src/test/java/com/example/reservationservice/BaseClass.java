package com.example.reservationservice;


import com.jayway.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest (classes = ReservationServiceApplication.class)
@RunWith(SpringRunner.class)
public class BaseClass {

    @Autowired
    ReservationRestController reservationRestController;

    @Before
    public void setUp() throws Exception {
        RestAssuredMockMvc.standaloneSetup(this.reservationRestController);
    }
}
