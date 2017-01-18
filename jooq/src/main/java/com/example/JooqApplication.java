package com.example;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

import static com.example.jooq.tables.Customer.CUSTOMER;
import static com.example.jooq.tables.Product.PRODUCT;

@SpringBootApplication
public class JooqApplication implements CommandLineRunner {


	private final CustomerRepository customerRepository;

	public JooqApplication(CustomerRepository customerRepository) {
		this.customerRepository = customerRepository;
	}

	@Override
	public void run(String... args) throws Exception {
		this.customerRepository.selectAll().forEach(System.out::println);
	}

	public static void main(String[] args) {
		SpringApplication.run(JooqApplication.class, args);
	}
}

@Data
@NoArgsConstructor
@AllArgsConstructor
class CustomerDTO {
	private Long id;
	private String email;
	private Set<ProductDTO> products = new HashSet<>();
}

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "customer")
class ProductDTO {

	private CustomerDTO customer;
	private String sku;
	private Long id;
}

@Repository
class CustomerRepository {

	private final DSLContext dslContext;

	public CustomerRepository(DSLContext dslContext) {
		this.dslContext = dslContext;
	}

	public Collection<CustomerDTO> selectAll() {
		Map<Record, Result<Record>> recordResultMap = dslContext
				.select()
				.from(CUSTOMER).leftJoin(PRODUCT).on(PRODUCT.CUSTOMER_ID.eq(CUSTOMER.ID))
				.orderBy(CUSTOMER.ID.asc())
				.fetch()
				.intoGroups(CUSTOMER.fields(CUSTOMER.ID));

		Collection<Result<Record>> values = recordResultMap.values();

		return values.stream().map(r -> {
			List<ProductDTO> products = r.sortAsc(PRODUCT.ID).into(ProductDTO.class).stream().filter(p -> p.getId() != null).collect(Collectors.toList());
			CustomerDTO customerDTO = r.into(CUSTOMER.ID, CUSTOMER.EMAIL).get(0).into(CustomerDTO.class);
			customerDTO.getProducts().addAll(products);
			return customerDTO;
		}).collect(Collectors.toList());
	}

}
