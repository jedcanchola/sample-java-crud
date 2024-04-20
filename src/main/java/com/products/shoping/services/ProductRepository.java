package com.products.shoping.services;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.products.shoping.models.Product;

public interface ProductRepository extends JpaRepository<Product, UUID> {

}
