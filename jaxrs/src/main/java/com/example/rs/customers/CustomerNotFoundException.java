package com.example.rs.customers;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @author <a href="mailto:josh@joshlong.com">Josh Long</a>
 */
@Data
public class CustomerNotFoundException extends Exception {

    private Long id;

    CustomerNotFoundException(Long id) {
        super("couldn't load " + Customer.class.getName() + "#" + id);
        this.id = id;
    }

}
