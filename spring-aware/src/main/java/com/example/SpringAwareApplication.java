package com.example;

import org.springframework.beans.factory.InjectionPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@SpringBootApplication
public class SpringAwareApplication {

    @Bean
    @Scope("prototype")
    Logger logger(InjectionPoint injectionPoint) {
        return Logger.getLogger(injectionPoint.getMember().getDeclaringClass().getName());
    }

    @Component
    public static class LoggingComponent {

        private final Logger logger ;

        public LoggingComponent(Logger logger) {
            this.logger = logger;
            this.logger.info("hello, world!");
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(SpringAwareApplication.class, args);
    }
}

