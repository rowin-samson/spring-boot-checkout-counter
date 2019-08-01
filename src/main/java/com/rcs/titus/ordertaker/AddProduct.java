package com.rcs.titus.ordertaker;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Optional;

import com.rcs.titus.ordertaker.model.OrderDetail;
import com.rcs.titus.ordertaker.model.OrderHeader;
import com.rcs.titus.ordertaker.model.Product;
import com.rcs.titus.ordertaker.repository.OrderDetailRepository;
import com.rcs.titus.ordertaker.repository.OrderHeaderRepository;
import com.rcs.titus.ordertaker.repository.ProductRepository;

public class AddProduct {
	private ProductRepository productRepo;
	private OrderDetailRepository orderDetailRepo;
	private OrderHeaderRepository orderHeaderRepo;

	// Constructor
	AddProduct(ProductRepository productRepository, OrderDetailRepository orderRepository, OrderHeaderRepository orderHeaderRepository) {
		this.productRepo = productRepository;
		this.orderDetailRepo = orderRepository;
		this.orderHeaderRepo = orderHeaderRepository;
	}

	private double amount = 0.0;
	public HashMap<String, OrderDetail> orderDetailMap = new HashMap<>();
	public OrderHeader orderHeader = null;
	private Integer oRNumber = 100000;
	private StringBuilder receiptContent = new StringBuilder();

	public void addProduct(String barCode, Double quantity) {
		// Only if we don't have order header
		if (orderHeader == null) {
			orderHeader = new OrderHeader();
			orderHeader.setCashierName("Cashier One");
			orderHeader.setOrderDate(new Date());
			orderHeader.setStatus("in-progress");
			orderHeader.setOrderNumber(oRNumber);
		}
		// Get the product by bar code
		Optional<Product> product = productRepo.findByBarCode(barCode);

		// If no quantity specify default it to one
		if (quantity == null) quantity = 1.0;

		// Check if we have product
		if (product.get() != null && orderHeader != null) {

			// Check if product already in the map then just add it
			if (orderDetailMap.containsKey(barCode) && orderDetailMap.get(barCode) != null) {
				double curQuantity = orderDetailMap.get(barCode).getQuantity() + quantity;
				double amount = product.get().getPrice() * curQuantity;
				orderDetailMap.get(barCode).setAmount(amount);
				orderDetailMap.get(barCode).setQuantity(curQuantity);
				orderDetailMap.get(barCode).setOrderHeader(orderHeader);
			} else {
				// Create new one
				OrderDetail orderDetail = new OrderDetail();
				double amount = product.get().getPrice() * quantity;
				orderDetail.setQuantity(quantity);
				orderDetail.setAmount(amount);
				orderDetail.setProduct(product.get());
				orderDetail.setOrderHeader(orderHeader);
				
				// Add to map
				orderDetailMap.put(barCode, orderDetail);
			}

			// Update amount
			this.setAmount(product.get().getPrice() * quantity);
		}
	}

	public void payOrder() {

		// String formatter to align text
		String strFormat = "%-30s %-20s";

		// Update order header and save it
		orderHeader.setTotal(this.amount);
		orderHeader.setStatus("paid");
		orderHeaderRepo.save(orderHeader);

		// Add receipt details
		receiptContent.append("Titus Grocery Store " + System.lineSeparator() + "(045) 123 4567");
		receiptContent.append(System.lineSeparator());
		receiptContent.append(
				System.lineSeparator() + "Order Number: " + orderHeader.getOrderNumber() + System.lineSeparator());
		receiptContent.append(String.format(strFormat, "Item", "Amount"));

		// Iterate order details map
		for (String key : orderDetailMap.keySet()) {

			// Add product details to the receipt
			receiptContent.append(System.lineSeparator());
			
			if(orderDetailMap.get(key).getProduct().isPromo() ) {
				receiptContent.append(String.format(strFormat, "*" + orderDetailMap.get(key).getProduct().getName() + "(" + orderDetailMap.get(key).getProduct().getPromoName() + ")",
						orderDetailMap.get(key).getAmount()));
			}else {
				receiptContent.append(String.format(strFormat, orderDetailMap.get(key).getProduct().getName(),
						orderDetailMap.get(key).getAmount()));
			}
			
			// Only if quantity is more than one
			if ( "piece".equalsIgnoreCase(orderDetailMap.get(key).getProduct().getType().trim()) && orderDetailMap.get(key).getQuantity() > 1)
				receiptContent.append(System.lineSeparator() + "  " + orderDetailMap.get(key).getQuantity().intValue() + orderDetailMap.get(key).getProduct().getUom() + " x "
						+ orderDetailMap.get(key).getProduct().getPrice());
			else if( "bulk".equalsIgnoreCase(orderDetailMap.get(key).getProduct().getType()) ) {
				receiptContent.append(System.lineSeparator() + "  " + orderDetailMap.get(key).getQuantity() + orderDetailMap.get(key).getProduct().getUom() + " x "
						+ orderDetailMap.get(key).getProduct().getPrice());
			}
			
			// Save the order details
			orderDetailRepo.save(orderDetailMap.get(key));
		}

		// Add some additional details for receipt
		receiptContent.append(System.lineSeparator());
		receiptContent.append(System.lineSeparator() + String.format(strFormat, "Total", orderHeader.getTotal())
				+ System.lineSeparator());
		receiptContent.append(System.lineSeparator());
		receiptContent.append("Cashier: " + orderHeader.getCashierName() + System.lineSeparator());
		receiptContent.append("Transaction Date: " + orderHeader.getOrderDate());

	}

	public void createReceipt() {

		// Create receipt file
		String fileName = "Receipt_" + oRNumber + ".txt";

		// try with resource
		try (FileWriter writer = new FileWriter(fileName); BufferedWriter bw = new BufferedWriter(writer)) {

			// Print the receipt details
			bw.write(receiptContent.toString());

			// Increment order number
			oRNumber++;

			// Reset some values
			this.receiptContent.setLength(0);
			this.orderDetailMap.clear();
			this.amount = 0.0;
			this.orderHeader = null;

		} catch (IOException e) {
			System.err.format("IOException: %s%n", e);
		}
	}

	public double getAmount() {
		return this.amount;
	}

	public void setAmount(Double amount) {
		this.amount += amount;
	}
}
