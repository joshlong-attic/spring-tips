package com.example.reservationclient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.BDDAssertions;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJson;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.contract.stubrunner.junit.StubRunnerRule;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collection;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

@RunWith(SpringRunner.class)
@SpringBootTest
//@AutoConfigureWireMock(port = 8082)
@AutoConfigureJson
@Slf4j
public class ReservationClientApplicationTests {

    @Rule
    public StubRunnerRule stubRunnerRule = new StubRunnerRule()
            .downloadStub("com.example", "reservation-service")
            .workOffline(true)
            .withPort(8082);

//    @Autowired
//    private ObjectMapper objectMapper;
/*
    @Test
    public void should_load_reservations() throws Exception {
        String jsonBody = this.objectMapper.writeValueAsString(Arrays.asList(
                new Reservation("Marcin")));

        log.info("json: " + jsonBody);

        stubFor(get(urlEqualTo("/reservations"))
                .willReturn(aResponse().withStatus(200).
                        withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE)
                        .withBody(jsonBody)));

        RestTemplate rt = new RestTemplate();
        ParameterizedTypeReference<Collection<Reservation>> ptr =
                new ParameterizedTypeReference<Collection<Reservation>>() {
                };
        ResponseEntity<Collection<Reservation>> response = rt.exchange(
                "http://localhost:8082/reservations", HttpMethod.GET, null, ptr);

        BDDAssertions.then(response.getStatusCodeValue()).isEqualTo(200);
        BDDAssertions.then(response.getBody()).contains(new Reservation("Marcin"));
    }*/

    private RestTemplate restTemplate = new RestTemplate();

    @Test
    public void should_load_reservations_integration() throws Exception {

        ParameterizedTypeReference<Collection<Reservation>> ptr =
                new ParameterizedTypeReference<Collection<Reservation>>() {
                };

        ResponseEntity<Collection<Reservation>> response = restTemplate.exchange(
                "http://localhost:8082/reservations", HttpMethod.GET, null, ptr);

        BDDAssertions.then(response.getStatusCodeValue()).isEqualTo(200);
        BDDAssertions.then(response.getBody()).contains(new Reservation("Marcin"));
    }

    @Test
    public void should_load_a_single_reservation() throws Throwable {


        ResponseEntity<Reservation> entity = restTemplate.getForEntity(
                "http://localhost:8082/reservations/22", Reservation.class);
        BDDAssertions.then(entity.getStatusCodeValue()).isEqualTo(200);
        BDDAssertions.then(entity.getBody()).isEqualTo(new Reservation("Leroy"));
    }
}
