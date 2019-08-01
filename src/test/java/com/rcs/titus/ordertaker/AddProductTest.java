package com.rcs.titus.ordertaker;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.rcs.titus.ordertaker.repository.OrderDetailRepository;
import com.rcs.titus.ordertaker.repository.OrderHeaderRepository;
import com.rcs.titus.ordertaker.repository.ProductRepository;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = OrdertakerApplication.class)
public class AddProductTest {
	@Autowired
	private ProductRepository productRepo;

	@Autowired
	private OrderDetailRepository orderDetailRepo;

	@Autowired
	private OrderHeaderRepository orderHeaderRepo;

	@DisplayName("Add product per piece and assert that amount is updated and equal")
	@Test
	public void testAddProductPerPiece() {
		// Product 100005 Price is 100
		// Product 100006 Price is 150
		AddProduct addProduct = new AddProduct(productRepo, orderDetailRepo, orderHeaderRepo);

		addProduct.addProduct("100005", 1.0);
		addProduct.addProduct("100006", 2.0);
		addProduct.addProduct("100005", 3.0);

		assertNotNull(addProduct.orderDetailMap);
		assertEquals(700, addProduct.getAmount());

	}

	@DisplayName("Add product bulk product and assert that amount is updated and equal")
	@Test
	public void testAddProuductInBulk() {
		// Product 100007 Price per kg 50
		// Product 100008 Price per kg 200
		AddProduct addProduct = new AddProduct(productRepo, orderDetailRepo, orderHeaderRepo);
		addProduct.addProduct("100007", .50);
		addProduct.addProduct("100008", 2.25);

		assertNotNull(addProduct.orderDetailMap);
		assertEquals(475, addProduct.getAmount());

	}

	@DisplayName("Add product with out specific quantity and assert that quantity must default to one")
	@Test
	public void testProductQuantitySpecified() {
		// Product 100005 Price is 100
		// Product 100006 Price is 150
		AddProduct addProduct = new AddProduct(productRepo, orderDetailRepo, orderHeaderRepo);
		addProduct.addProduct("100005", 2.0);
		addProduct.addProduct("100006", null);

		assertNotNull(addProduct.orderDetailMap);
		assertEquals(350, addProduct.getAmount());

	}

	@DisplayName("Add promo product and assert the amount is updated")
	@Test
	public void testAddPromoProduct() {
		// Product 100009 is buy one get one for free price is 50
		// Product 100010 is buy two get one for free price is 50
		AddProduct addProduct = new AddProduct(productRepo, orderDetailRepo, orderHeaderRepo);
		addProduct.addProduct("100009", 2.0);
		addProduct.addProduct("100010", 1.0);

		assertNotNull(addProduct.orderDetailMap);
		assertTrue(addProduct.orderDetailMap.get("100009").getProduct().isPromo());
		assertTrue(addProduct.orderDetailMap.get("100010").getProduct().isPromo());
		assertEquals(150, addProduct.getAmount());

	}

	@DisplayName("Pay transaction and assert the transaction status is paid")
	@Test
	public void testPayOrder() {
		// Product 100005 Price is 100
		// Product 100007 Price per kg 50
		// Product 100009 is buy one get one for free price is 50

		AddProduct addProduct = new AddProduct(productRepo, orderDetailRepo, orderHeaderRepo);
		addProduct.addProduct("100005", 2.0);
		addProduct.addProduct("100007", 50.0);
		addProduct.addProduct("100009", 1.0);
		addProduct.addProduct("100005", null);
		addProduct.payOrder();

		assertNotNull(addProduct.orderDetailMap);
		assertEquals("paid", addProduct.orderHeader.getStatus());
		assertEquals(2850, addProduct.getAmount());

	}

	@Test
	@DisplayName("Create two transactions and each transaction must create tow separate receipt")
	public void testPrintReceipt() {

		AddProduct addProduct = new AddProduct(productRepo, orderDetailRepo, orderHeaderRepo);
		addProduct.addProduct("100005", 2.0);
		addProduct.addProduct("100007", 50.0);
		addProduct.addProduct("100009", 1.0);
		addProduct.addProduct("100005", null);
		addProduct.payOrder();

		assertNotNull(addProduct.orderDetailMap);
		assertEquals("paid", addProduct.orderHeader.getStatus());
		assertEquals(2850, addProduct.getAmount());

		File oROneFile = new File("Receipt_" + addProduct.orderHeader.getOrderNumber() + ".txt");

		addProduct.createReceipt();

		assertTrue(oROneFile.exists());

		addProduct.addProduct("100005", 1.0);
		addProduct.addProduct("100006", 1.0);
		addProduct.addProduct("100005", 3.0);
		addProduct.addProduct("100007", .50);
		addProduct.addProduct("100008", 2.25);
		addProduct.payOrder();
		assertNotNull(addProduct.orderDetailMap);
		assertEquals(1025, addProduct.getAmount());

		File oRTwoFile = new File("Receipt_" + addProduct.orderHeader.getOrderNumber() + ".txt");
		addProduct.createReceipt();

		assertTrue(oRTwoFile.exists());

	}
}
