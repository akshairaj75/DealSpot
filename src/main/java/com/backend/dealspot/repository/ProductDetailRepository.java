package com.backend.dealspot.repository;

import com.backend.dealspot.entity.ProductDetail;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductDetailRepository extends JpaRepository<ProductDetail, Long> {

    List<ProductDetail> findByProductId(Long productId);
}
