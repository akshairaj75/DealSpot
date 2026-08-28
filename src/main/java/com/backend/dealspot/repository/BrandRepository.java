package com.backend.dealspot.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.backend.dealspot.entity.Brand;

public interface BrandRepository extends JpaRepository<Brand, Long> {
    Page<Brand> findByNameEnContainingIgnoreCaseOrNameArContainingIgnoreCase(String nameEn, String nameAr, Pageable pageable);

    Page<Brand> findByFeaturedTrue(Pageable pageable);

    @Query("SELECT b FROM Brand b WHERE (LOWER(b.nameEn) LIKE LOWER(CONCAT('%', :q, '%')) OR LOWER(b.nameAr) LIKE LOWER(CONCAT('%', :q, '%'))) AND b.featured = true")
    Page<Brand> searchFeaturedBrands(@Param("q") String q, Pageable pageable);
}
