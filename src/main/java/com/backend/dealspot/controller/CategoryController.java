package com.backend.dealspot.controller;

import com.backend.dealspot.dto.category.CategoryDto;
import com.backend.dealspot.dto.category.CategoryRequestDto;
import com.backend.dealspot.service.CategoryService;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/dealspot/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping(value = "/create")
    public ResponseEntity<CategoryDto> createCategory(
            @RequestPart("data") CategoryRequestDto dto,
            @RequestPart(value = "file", required = false) MultipartFile file) {
        CategoryDto createdCategory = categoryService.createCategory(dto, file);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCategory);
    }

    @GetMapping("/fetch-categories")
    public ResponseEntity<List<CategoryDto>> fetchCategories() {
        List<CategoryDto> dto = categoryService.fetchCategories();
        return ResponseEntity.ok(dto);
    }
}
