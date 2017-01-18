package com.example;

import com.example.jooq.tables.Customer;
import com.example.jooq.tables.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Record2;
import org.jooq.Result;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SpringBootApplication
public class JooqApplication {

	@Bean
	CommandLineRunner demo(CustomerRepository customerRepository) {
		return args -> {
			customerRepository.selectAllCustomers()
					.forEach(System.out::println);
		};
	}

	public static void main(String[] args) {
		SpringApplication.run(JooqApplication.class, args);
	}
}

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "customer")
class ProductDTO {
	private Long id;
	private String sku;
	private CustomerDTO customer;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
class CustomerDTO {
	private Long id;
	private String email;
	private Collection<ProductDTO> products = new HashSet<>();
}

@Repository
@Transactional
class CustomerRepository {

	private final DSLContext dslContext;

	public CustomerRepository(DSLContext dslContext) {
		this.dslContext = dslContext;
	}

	public Collection<CustomerDTO> selectAllCustomers() {

		Map<Record, Result<Record>> recordResultMap =
				this.dslContext.select().from(Customer.CUSTOMER)
						.leftJoin(Product.PRODUCT)
						.on(Customer.CUSTOMER.ID.eq(Product.PRODUCT.CUSTOMER_ID))
						.fetch()
						.intoGroups(Customer.CUSTOMER.fields());
		return recordResultMap
				.values()
				.stream()
				.map(r -> {
					Record2<Long, String> record2 = r.into(Customer.CUSTOMER.ID, Customer.CUSTOMER.EMAIL).get(0);
					Long customerId = record2.value1();
					String email = record2.value2();
					List<ProductDTO> productDTOS =
							r.sortAsc(Customer.CUSTOMER.ID).into(ProductDTO.class)
									.stream()
									.filter(pdto -> pdto.getId() != null)
									.collect(Collectors.toList());
					return new CustomerDTO(customerId, email, productDTOS);
				})
				.collect(Collectors.toList());
	}

	public void insertCustomer(String email) {
		this.dslContext
				.insertInto(Customer.CUSTOMER)
				.columns(Customer.CUSTOMER.EMAIL)
				.values(email);
	}


}