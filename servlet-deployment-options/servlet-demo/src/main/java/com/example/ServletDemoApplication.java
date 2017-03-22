package com.example;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.jetty.JettyEmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.undertow.UndertowEmbeddedServletContainerFactory;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.*;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;

@SpringBootApplication
public class ServletDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServletDemoApplication.class, args);
	}

	@Bean
	FilterRegistrationBean filterRegistrationBean() {
		MyCustomFilter myCustomFilter = new MyCustomFilter();
		return new FilterRegistrationBean(myCustomFilter);
	}
}

@Component
class MyCustomizer implements EmbeddedServletContainerCustomizer {

	private final Log log = LogFactory.getLog(getClass());

	@Override
	public void customize(ConfigurableEmbeddedServletContainer container) {
		if (TomcatEmbeddedServletContainerFactory.class.isAssignableFrom(container.getClass())) {
			TomcatEmbeddedServletContainerFactory tc = TomcatEmbeddedServletContainerFactory.class.cast(container);
			log.info("is tomcat");
		}
		if (UndertowEmbeddedServletContainerFactory.class.isAssignableFrom(container.getClass())) {
			UndertowEmbeddedServletContainerFactory undertow =
					UndertowEmbeddedServletContainerFactory.class.cast(container);
			log.info("is undertow");
		}
		if (JettyEmbeddedServletContainerFactory.class.isAssignableFrom(container.getClass())) {
			JettyEmbeddedServletContainerFactory jetty =
					JettyEmbeddedServletContainerFactory.class.cast(container);
			log.info("is jetty");
		}
	}
}

class MyCustomFilter implements Filter {

	private final Log log = LogFactory.getLog(getClass());

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		this.log.info("init(" + filterConfig + ")");
	}

	@Override
	public void doFilter(
			ServletRequest servletRequest,
			ServletResponse servletResponse,
			FilterChain filterChain) throws IOException, ServletException {
		log.info("doFilter(" + servletRequest + ", " + servletResponse + ")");
		filterChain.doFilter(servletRequest, servletResponse);
	}

	@Override
	public void destroy() {
		log.info("destroy()");
	}
}

@RestController
class GreetingsRestController {

	@GetMapping("/greetings")
	Map<String, String> greetings() {
		return Collections.singletonMap("greeting", "Hello world");
	}
}