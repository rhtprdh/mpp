package com.soms.product_service.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.soms.product_service.model.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {}
