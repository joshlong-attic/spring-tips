package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@SpringBootApplication
public class XaDsMqApplication {

	public static void main(String[] args) {
		SpringApplication.run(XaDsMqApplication.class, args);
	}

	public static final String DESTINATION = "messages";

	@Service
	public static class MessageNotificationListener {

		@JmsListener(destination = DESTINATION)
		public void onNewMessage(String id) {
			System.out.println("message ID: " + id);
		}
	}

	@RestController
	public static class XaApiRestController {

		private final JmsTemplate jmsTemplate;
		private final JdbcTemplate jdbcTemplate;

		public XaApiRestController(JmsTemplate jmsTemplate, JdbcTemplate jdbcTemplate) {
			this.jmsTemplate = jmsTemplate;
			this.jdbcTemplate = jdbcTemplate;
		}

		@GetMapping
		public Collection<Map<String, String>> read() {
			return this.jdbcTemplate.query("select * from MESSAGE", (resultSet, i) -> {
				Map<String, String> msg = new HashMap<>();
				msg.put("id", resultSet.getString("ID"));
				msg.put("message", resultSet.getString("MESSAGE"));
				return msg;
			});
		}

		@PostMapping
		@Transactional
		public void write(@RequestBody Map<String, String> payload,
		                  @RequestParam Optional<Boolean> rollback) {

			String id = UUID.randomUUID().toString();

			String name = payload.get("name");
			String msg = "Hello, " + name + "!";

			this.jdbcTemplate.update("insert into MESSAGE(ID, MESSAGE) VALUES( ?, ?)",
					id, msg);

			this.jmsTemplate.convertAndSend(DESTINATION, id);

			if (rollback.orElse(false)) {
				throw new RuntimeException("couldn't write the message!");
			}
		}
	}

}
