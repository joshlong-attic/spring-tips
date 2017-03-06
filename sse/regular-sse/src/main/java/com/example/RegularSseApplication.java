package com.example;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.file.Files;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@SpringBootApplication
@RestController
public class RegularSseApplication {

	private final Map<String, SseEmitter> sseEmitters = new ConcurrentHashMap<>();

	@Bean
	IntegrationFlow inbound(@Value("${input:file://${HOME}/Desktop/in}") File file)
			throws Throwable {

		return IntegrationFlows
				.from(Files
						.inboundAdapter(file)
						.autoCreateDirectory(true), c -> c.poller(ps -> ps.fixedRate(1000)))
				.transform(File.class, File::getAbsolutePath)
				.handle(String.class, (s, map) -> {
					notifyFile(s);
					return null;
				})
				.get();
	}

	@GetMapping("/files/{name}")
	SseEmitter files(@PathVariable String name) {
		SseEmitter sse = new SseEmitter(60 * 1000L);
		this.sseEmitters.put(name, sse);
		return sse;
	}

	private void notifyFile(String path) {
		this.sseEmitters.forEach((String k, SseEmitter sse) -> {
			try {
				sse.send(path, MediaType.APPLICATION_JSON);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		});
	}

	public static void main(String[] args) {
		SpringApplication.run(RegularSseApplication.class, args);
	}
}
