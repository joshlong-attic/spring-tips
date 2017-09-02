package com.example.rs.customers;

import lombok.Data;

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
