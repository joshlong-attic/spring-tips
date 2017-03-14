package service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest
@RunWith(SpringRunner.class)
public class ReactiveServiceApplicationTest {

	private WebTestClient client;

	@Before
	public void before() throws Throwable {
		this.client = WebTestClient.bindToServer()
				.baseUrl("http://localhost:8080")
				.build();
	}

	@Test
	public void eventById() throws Exception {

		this.client.get()
				.uri("/events/2")
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isOk();
	}

}