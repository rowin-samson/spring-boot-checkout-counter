package com.rcs.titus.ordertaker.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.rcs.titus.ordertaker.model.OrderDetail;
import com.rcs.titus.ordertaker.repository.OrderDetailRepository;

import lombok.RequiredArgsConstructor;

@Service

@RequiredArgsConstructor
public class OrderDetailService {

	private OrderDetailRepository orderDetailRepository;
	
	// Dependency injection
	public OrderDetailService( OrderDetailRepository orderDetailRepo ){
		this.orderDetailRepository = orderDetailRepo;
	}
	
	public List<OrderDetail> findAll(){
		return orderDetailRepository.findAll();
	}
	
	public Optional<OrderDetail> findById(Long id){
		return orderDetailRepository.findById(id);
	}
	
	public OrderDetail save(OrderDetail orderDetail)
	{
		return orderDetailRepository.save(orderDetail);
	}
	
	public void deleteById(Long id) {
		orderDetailRepository.deleteById(id);
	}
}
