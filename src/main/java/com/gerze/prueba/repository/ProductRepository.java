package com.gerze.prueba.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gerze.prueba.model.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findAllByStateTrue();
}
