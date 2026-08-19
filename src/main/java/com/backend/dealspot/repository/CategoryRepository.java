package com.backend.dealspot.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import com.backend.dealspot.entity.Category;

public interface CategoryRepository extends JpaRepository <Category, Integer> {
    
    boolean existsByNameEnIgnoreCaseOrNameArIgnoreCase(String nameEn, String nameAr);

    List<Category> findAllByOrderBySortOrderAsc();

}
