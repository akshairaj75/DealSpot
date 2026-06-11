package com.backend.dealspot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.backend.dealspot.entity.Brand;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Long> {
}
