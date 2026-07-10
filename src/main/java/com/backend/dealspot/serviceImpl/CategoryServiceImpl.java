package com.backend.dealspot.serviceImpl;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.backend.dealspot.dto.category.CategoryDto;
import com.backend.dealspot.dto.category.CategoryRequestDto;
import com.backend.dealspot.entity.AdminUser;
import com.backend.dealspot.entity.Category;
import com.backend.dealspot.repository.AdminUserRepository;
import com.backend.dealspot.repository.CategoryRepository;
import com.backend.dealspot.security.CustomUserPrincipal;
import com.backend.dealspot.service.CategoryService;

import jakarta.transaction.Transactional;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final FileStorageService fileStorageService;
    private final AdminUserRepository adminUserRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository,
            FileStorageService fileStorageService,
            AdminUserRepository adminUserRepository) {
        this.categoryRepository = categoryRepository;
        this.fileStorageService = fileStorageService;
        this.adminUserRepository = adminUserRepository;
    }

    @Transactional
    @Override
    public CategoryDto createCategory(CategoryRequestDto dto, MultipartFile file) {
        if (dto == null) {
            throw new IllegalArgumentException("Category request is required");
        }

        if (categoryRepository.existsByNameEnIgnoreCaseOrNameArIgnoreCase(dto.getNameEn(), dto.getNameAr())) {
            throw new IllegalArgumentException("Category with the same English or Arabic name already exists");
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
                String imageFile = fileStorageService.storeFile(file,"categories");
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

    @Override
    public CategoryDto updateCategory(Integer categoryId, CategoryRequestDto dto, MultipartFile file,
            CustomUserPrincipal authUser) {
        
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        Optional<AdminUser> user = adminUserRepository.findById(authUser.getId());

        if (dto.getNameEn() != null && !dto.getNameEn().isEmpty()) {
            category.setNameEn(dto.getNameEn());
        }

        if (dto.getNameAr() != null && !dto.getNameAr().isEmpty()) {
            category.setNameAr(dto.getNameAr());
        }

        if (dto.getIconSlug() != null && !dto.getIconSlug().isEmpty()) {
            category.setIconSlug(dto.getIconSlug());
        }

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
                String imageFile = fileStorageService.storeFile(file, "categories");
                category.setImageUrl(imageFile);
            } catch (IOException e) {
                throw new RuntimeException("Failed to upload category image", e);
            }
        }
        Category saved = categoryRepository.save(category);
        return CategoryDto.fromEntity(saved);
    }
}
