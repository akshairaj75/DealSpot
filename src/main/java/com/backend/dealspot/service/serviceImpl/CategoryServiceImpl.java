package com.backend.dealspot.service.serviceImpl;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.backend.dealspot.dto.category.CategoryDto;
import com.backend.dealspot.dto.category.CategoryRequestDto;
import com.backend.dealspot.entity.Category;
import com.backend.dealspot.repository.CategoryRepository;
import com.backend.dealspot.service.CategoryService;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final FileStorageService fileStorageService;

    public CategoryServiceImpl(CategoryRepository categoryRepository,
            FileStorageService fileStorageService) {
        this.categoryRepository = categoryRepository;
        this.fileStorageService = fileStorageService;
    }

    @Override
    public CategoryDto createCategory(CategoryRequestDto dto, MultipartFile file) {
        if (dto == null) {
            throw new IllegalArgumentException("Category request is required");
        }

        Category category = new Category();

        category.setNameEn(dto.getNameEn());
        category.setNameAr(dto.getNameAr());
        category.setIconSlug(dto.getIconSlug());

        if (dto.getSortOrder() != null) {
            category.setSortOrder(dto.getSortOrder());
        }
        if (dto.getActive() != null) {
            category.setActive(dto.getActive());
        }
        if (dto.getParentId() != null) {
            Category parentCategory = categoryRepository.findById(dto.getParentId())
                    .orElseThrow(() -> new IllegalArgumentException("Parent category not found"));

            category.setParent(parentCategory);
        }
        if (file != null && !file.isEmpty()) {
            try {
                String imageFile = fileStorageService.storeFile(file);
                category.setImageUrl(imageFile);
            } catch (IOException e) {
                throw new RuntimeException("Failed to upload category image", e);
            }
        }
        Category saved = categoryRepository.save(category);

        return CategoryDto.fromEntity(saved);
    }

    @Override
    public List<CategoryDto> fetchCategories() {
        List<Category> result = categoryRepository.findAll();
        return result.stream()
                .map(CategoryDto::fromEntity)
                .toList();
    }
}
