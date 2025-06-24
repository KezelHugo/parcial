package com.msorders.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.msorders.model.Order;

public interface OrderRepository extends JpaRepository<Order, Long>  {

  List<Order> findByCustomerContainingIgnoreCase(String customer);

}
