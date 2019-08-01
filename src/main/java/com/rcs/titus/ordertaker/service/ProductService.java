package com.rcs.titus.ordertaker.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rcs.titus.ordertaker.model.Product;
import com.rcs.titus.ordertaker.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service

@RequiredArgsConstructor
public class ProductService {

	private ProductRepository productRepository;
	
//	// Dependency injection through constructor
	public ProductService( ProductRepository productRepo ){
		this.productRepository = productRepo;
	}
	
	public List<Product> findAll(){
		return this.productRepository.findAll();
	}
	
	public Optional<Product> findById(Long id){		
		return this.productRepository.findById(id);
	}
	
	public Optional<Product> findByBarCode(String barCode){		
		return this.productRepository.findByBarCode(barCode);
	}
	
	public Product save(Product product)
	{
		return this.productRepository.save(product);
	}
	
	public void deleteById(Long id) {
		this.productRepository.deleteById(id);
	}
}
