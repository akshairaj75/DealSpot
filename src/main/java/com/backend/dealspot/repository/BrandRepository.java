package com.backend.dealspot.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.backend.dealspot.entity.Brand;

public interface BrandRepository extends JpaRepository<Brand, Long> {
    Page<Brand> findByNameEnContainingIgnoreCaseOrNameArContainingIgnoreCase(String nameEn, String nameAr, Pageable pageable);
}
