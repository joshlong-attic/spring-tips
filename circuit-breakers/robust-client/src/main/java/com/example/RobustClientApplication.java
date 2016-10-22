package com.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.CircuitBreaker;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.annotation.Recover;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@EnableRetry
@SpringBootApplication
public class RobustClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(RobustClientApplication.class, args);
    }
}

@RestController
class ShakyRestController {

    private final ShakyBusinessService shakyBusinessService;

    @Autowired
    public ShakyRestController(ShakyBusinessService shakyBusinessService) {
        this.shakyBusinessService = shakyBusinessService;
    }

    @GetMapping("/boom")
    public int boom() throws Exception {
        return this.shakyBusinessService.deriveNumber();
    }
}

class BoomException extends RuntimeException {
    BoomException(String message) {
        super(message);
    }
}

@Service
class ShakyBusinessService {

    @Recover
    public int fallback(BoomException ex) {
        return 2;
    }

    @CircuitBreaker(include = BoomException.class)
    public int deriveNumber() throws Exception {
        System.out.println("calling deriveNumber()");
        if (Math.random() > .5) {
            Thread.sleep(1000 * 3);
            throw new BoomException("Boom!");
        }
        return 1;
    }
}