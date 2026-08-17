package com.backend.dealspot.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.backend.dealspot.dto.category.CategoryDto;
import com.backend.dealspot.dto.category.CategoryRequestDto;

public interface CategoryService {

    CategoryDto createCategory(CategoryRequestDto dto, MultipartFile file);

    List<CategoryDto> fetchCategories();

    CategoryDto updateCategory(Integer categoryId, CategoryRequestDto dto, MultipartFile file);

    void deleteCategory(Integer categoryId);

}
