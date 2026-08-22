package com.backend.dealspot.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.backend.dealspot.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

    boolean existsByBrandId(Long id);

    @Query("SELECT p FROM Product p WHERE " +
           "(:categoryId IS NULL OR p.category.id = :categoryId OR (p.category.parent IS NOT NULL AND p.category.parent.id = :categoryId)) AND " +
           "(:brandId IS NULL OR (p.brand IS NOT NULL AND p.brand.id = :brandId)) AND " +
           "(:search IS NULL OR :search = '' OR " +
           " LOWER(p.nameEn) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           " LOWER(p.nameAr) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           " LOWER(p.sku) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           " LOWER(p.barcode) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           " (p.brand IS NOT NULL AND (LOWER(p.brand.nameEn) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(p.brand.nameAr) LIKE LOWER(CONCAT('%', :search, '%')))))")
    Page<Product> searchProducts(
            @Param("search") String search,
            @Param("categoryId") Integer categoryId,
            @Param("brandId") Long brandId,
            Pageable pageable
    );
}
