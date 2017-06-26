package com.example.reservationclient;

import org.assertj.core.api.BDDAssertions;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJson;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.stubrunner.junit.StubRunnerRule;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.util.Collection;

@AutoConfigureJson
@RunWith(SpringRunner.class)
@SpringBootTest
public class ReservationClientApplicationTests {

    @Rule
    public StubRunnerRule stubRunnerRule = new StubRunnerRule()
            .downloadStub("com.example", "reservation-service")
            .workOffline(true)
            .withPort(8082);

    RestTemplate restTemplate = new RestTemplate();

    @Test
    public void test_should_return_reservations_integration() throws Throwable {

        ParameterizedTypeReference<Collection<Reservation>> ptr =
                new ParameterizedTypeReference<Collection<Reservation>>() {
                };

        ResponseEntity<Collection<Reservation>> responseEntity = restTemplate.exchange(
                "http://localhost:8082/reservations", HttpMethod.GET, null, ptr);

        BDDAssertions.then(responseEntity.getStatusCodeValue()).isEqualTo(200);
        BDDAssertions.then(responseEntity.getBody()).contains(new Reservation("Marcin"));

    }

}
