package com.backend.dealspot.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.backend.dealspot.dto.category.CategoryDto;
import com.backend.dealspot.dto.category.CategoryRequestDto;

public interface CategoryService {

    CategoryDto createCategory(CategoryRequestDto dto,  MultipartFile file);

    List<CategoryDto> fetchCategories();

}
