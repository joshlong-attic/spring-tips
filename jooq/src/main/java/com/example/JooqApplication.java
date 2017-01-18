package com.example;

import com.example.jooq.tables.Customer;
import com.example.jooq.tables.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.jooq.DSLContext;
import org.jooq.Record2;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Repository;

import java.util.*;

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

		Map<Long, CustomerDTO> customerDTOMap = new HashMap<>();

		this.dslContext
				.select()
				.from(Customer.CUSTOMER)
				.leftJoin(Product.PRODUCT).on(Customer.CUSTOMER.ID.eq(Product.PRODUCT.CUSTOMER_ID))
				.forEach(record -> {
					CustomerDTO customerDTO = record.into(Customer.CUSTOMER.ID, Customer.CUSTOMER.EMAIL).into(CustomerDTO.class);
					customerDTOMap.putIfAbsent(customerDTO.getId(), customerDTO);

					// products
					Set<ProductDTO> products = customerDTOMap.get(customerDTO.getId()).getProducts();
					Optional.ofNullable(record.into(Product.PRODUCT.ID).value1()).ifPresent(productId -> {
						Record2<Long, String> record2 = record.into(Product.PRODUCT.ID, Product.PRODUCT.SKU);
						ProductDTO productDTO = new ProductDTO(customerDTO, record2.value2(), record2.value1());
						products.add(productDTO);
					});
				});
		return customerDTOMap.values();
	}

}
