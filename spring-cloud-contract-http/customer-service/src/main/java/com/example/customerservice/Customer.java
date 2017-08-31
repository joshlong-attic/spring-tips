package com.example.customerservice;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * @author <a href="mailto:josh@joshlong.com">Josh Long</a>
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
class Customer {
    @Id
    @GeneratedValue
    private Long id;
    private String name;
}
