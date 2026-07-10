package com.backend.dealspot.controller;

import com.backend.dealspot.dto.category.CategoryDto;
import com.backend.dealspot.dto.category.CategoryRequestDto;
import com.backend.dealspot.security.CustomUserPrincipal;
import com.backend.dealspot.service.CategoryService;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

    @PostMapping("/create/bulk")
    public ResponseEntity<List<CategoryDto>> createCategories(
            @RequestBody List<CategoryRequestDto> dtos) {

        List<CategoryDto> categories = dtos.stream()
                .map(dto -> categoryService.createCategory(dto, null))
                .toList();

        return ResponseEntity.status(HttpStatus.CREATED).body(categories);
    }

    @GetMapping("/fetch-categories")
    public ResponseEntity<List<CategoryDto>> fetchCategories() {
        List<CategoryDto> dto = categoryService.fetchCategories();
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/edit/{categoryId}")
    public ResponseEntity<CategoryDto> editCategory(
            @RequestPart("data") CategoryRequestDto dto,
            @RequestPart(value = "file", required = false) MultipartFile file,
            @PathVariable Integer categoryId,
            @AuthenticationPrincipal CustomUserPrincipal authUser) {
        CategoryDto editedCategory = categoryService.updateCategory(categoryId, dto, file, authUser);
        return ResponseEntity.ok(editedCategory);
    }
}
