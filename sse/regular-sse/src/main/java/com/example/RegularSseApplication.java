package com.example;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
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

	private final Log log = LogFactory.getLog(getClass());

	private final Map<String, SseEmitter> sseEmitters = new ConcurrentHashMap<>();

	@Bean
	IntegrationFlow inbound(@Value("${input:file://${HOME}/Desktop/in}") File file) throws Throwable {
		return IntegrationFlows
				.from(Files
						.inboundAdapter(file)
						.autoCreateDirectory(true), c -> c.poller(ps -> ps.fixedRate(1000)))
				.transform(File.class, File::getAbsolutePath)
				.handle(String.class, (path, map) -> {
					notifyFile(path);
					return null;
				})
				.get();
	}

	@GetMapping("/files/{name}")
	SseEmitter files(@PathVariable String name) {
		log.info("creating SSE for " + name + ".");
		this.sseEmitters.put(name, new SseEmitter(60 * 1000 * 1000L));
		return this.sseEmitters.get(name);
	}

	private void notifyFile(String path) {
		this.sseEmitters.values().forEach(
				sse -> {
					try {
						sse.send(path);
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
				}
		);
	}

	public static void main(String[] args) {
		SpringApplication.run(RegularSseApplication.class, args);
	}
}
