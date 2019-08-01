package com.rcs.titus.ordertaker.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity

public class Product {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String barCode;
	private String name;
	private Double price;
	private String type;
	private String uom;
	private boolean isPromo;
	private String promoName;
	private boolean isActive;
	
	public Product( String barcode, String name, Double price, boolean isActive)
	{
		this.barCode = barcode;
		this.name = name;
		this.price = price;
		this.isActive = isActive;
	}
}
