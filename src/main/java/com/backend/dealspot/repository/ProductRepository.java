package com.backend.dealspot.repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.backend.dealspot.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long>{

}
