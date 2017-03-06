package com.example;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.channel.MessageChannels;
import org.springframework.integration.file.dsl.Files;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import java.io.File;

@SpringBootApplication
@RestController
public class ReactiveSseApplication {

	private final Log log = LogFactory.getLog(getClass());

	@Bean
	SubscribableChannel channel() {
		return MessageChannels.publishSubscribe().get();
	}

	@Bean
	IntegrationFlow inbound(@Value("${input:file://${HOME}/Desktop/in}") File file) throws Throwable {
		return IntegrationFlows
				.from(Files
						.inboundAdapter(file)
						.autoCreateDirectory(true), c -> c.poller(ps -> ps.fixedRate(1000)))
				.transform(File.class, File::getAbsolutePath)
				.channel(channel())
				.get();
	}

	@GetMapping(value = "/files/{name}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	Flux<String> files(@PathVariable String name) {
		log.info("creating SSE for " + name + ".");
		SubscribableChannel channel = channel();
		return Flux
				.create(unsafeSink -> {
					FluxSink<String> sink = unsafeSink.serialize();
					MessageHandler messageHandler =
							message -> sink.next(String.class.cast(message.getPayload()));
					sink.setCancellation(() -> channel.unsubscribe(messageHandler));
					channel.subscribe(messageHandler);
				});
	}

	public static void main(String[] args) {
		SpringApplication.run(ReactiveSseApplication.class, args);
	}
}