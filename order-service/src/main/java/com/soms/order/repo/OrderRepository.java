package com.soms.order.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.soms.order.model.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
