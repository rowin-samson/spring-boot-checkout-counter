package com.rcs.titus.ordertaker.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.rcs.titus.ordertaker.model.OrderHeader;
import com.rcs.titus.ordertaker.repository.OrderHeaderRepository;

import lombok.RequiredArgsConstructor;

@Service

@RequiredArgsConstructor
public class OrderHeaderService {

	private OrderHeaderRepository orderHeaderRepository;
	
	// Dependency injection through constructor
	public OrderHeaderService( OrderHeaderRepository orderHeaderRepo ){
		this.orderHeaderRepository = orderHeaderRepo;
	}
	
	public List<OrderHeader> findAll(){
		return this.orderHeaderRepository.findAll();
	}
	
	public Optional<OrderHeader> findById(Long id){
		return this.orderHeaderRepository.findById(id);
	}
	
	public OrderHeader save( OrderHeader orderHeader ) {
		return this.orderHeaderRepository.save(orderHeader);
	}
	
	public void deleteById( Long id ) {
		this.orderHeaderRepository.deleteById(id);
	}
}
