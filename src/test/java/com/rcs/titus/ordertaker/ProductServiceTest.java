package com.rcs.titus.ordertaker;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.rcs.titus.ordertaker.model.Product;
import com.rcs.titus.ordertaker.repository.ProductRepository;

@ExtendWith(SpringExtension.class)

@SpringBootTest(classes = OrdertakerApplication.class)
public class ProductServiceTest {

	@Autowired
	private ProductRepository productRepo;

	@DisplayName("Test that we can add product and find product by barcode")
	@Test
	public void testAddProduct() {
		Product product = new Product("test-100001", "Test", 10.0, true);		
		assertEquals("test-100001", product.getBarCode());
		productRepo.save(product);
		
		Optional<Product> findProduct = productRepo.findByBarCode("test-100001");
		assertEquals("test-100001", findProduct.get().getBarCode());
	}
}
