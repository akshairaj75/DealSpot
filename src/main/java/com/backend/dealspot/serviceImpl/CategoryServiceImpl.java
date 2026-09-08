package com.backend.dealspot.serviceImpl;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.backend.dealspot.dto.category.CategoryDto;
import com.backend.dealspot.dto.category.CategoryOrderDto;
import com.backend.dealspot.dto.category.CategoryRequestDto;
import com.backend.dealspot.entity.Category;
import com.backend.dealspot.enums.AuditAction;
import com.backend.dealspot.repository.CategoryRepository;
import com.backend.dealspot.service.AuditLogService;
import com.backend.dealspot.service.CategoryService;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final FileStorageService fileStorageService;
    private final AuditLogService auditLogService;

    public CategoryServiceImpl(CategoryRepository categoryRepository,
            FileStorageService fileStorageService,
            AuditLogService auditLogService) {
        this.categoryRepository = categoryRepository;
        this.fileStorageService = fileStorageService;
        this.auditLogService = auditLogService;
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
        category.setIconSlug(dto.getIconSlug() != null ? dto.getIconSlug() : "folder");

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

        java.util.Map<String, Object> createAuditPayload = new java.util.HashMap<>();
        createAuditPayload.put("nameEn", saved.getNameEn());
        createAuditPayload.put("nameAr", saved.getNameAr());
        createAuditPayload.put("iconSlug", saved.getIconSlug());
        auditLogService.logAction("CATEGORY", saved.getId().longValue(), AuditAction.CREATE, createAuditPayload);

        return CategoryDto.fromEntity(saved);
    }

    @Override
    public List<CategoryDto> fetchCategories() {
        List<Category> result = categoryRepository.findAllByOrderBySortOrderAsc();
        return result.stream()
                .map(CategoryDto::fromEntity)
                .toList();
    }

    @Transactional
    @Override
    public void updateCategoriesOrder(List<CategoryOrderDto> orderList) {
        if (orderList == null || orderList.isEmpty()) {
            return;
        }
        for (CategoryOrderDto item : orderList) {
            if (item.getId() != null && item.getSortOrder() != null) {
                categoryRepository.findById(item.getId()).ifPresent(cat -> {
                    cat.setSortOrder(item.getSortOrder());
                    categoryRepository.save(cat);
                });
            }
        }
    }

    @Transactional
    @Override
    public CategoryDto updateCategory(Integer categoryId, CategoryRequestDto dto, MultipartFile file) {
        
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));

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
            if (dto.getParentId().equals(categoryId)) {
                throw new IllegalArgumentException("A category cannot be its own parent");
            }
            Category parentCategory = categoryRepository.findById(dto.getParentId())
                    .orElseThrow(() -> new IllegalArgumentException("Parent category not found"));
            category.setParent(parentCategory);
        } else {
            category.setParent(null);
        }

        if (file != null && !file.isEmpty()) {
            try {
                if (category.getImageUrl() != null) {
                    fileStorageService.deleteFile(category.getImageUrl(), "categories");
                }
                String imageFile = fileStorageService.storeFile(file, "categories");
                category.setImageUrl(imageFile);
            } catch (IOException e) {
                throw new RuntimeException("Failed to upload category image", e);
            }
        }
        Category saved = categoryRepository.save(category);

        java.util.Map<String, Object> updateAuditPayload = new java.util.HashMap<>();
        updateAuditPayload.put("nameEn", saved.getNameEn());
        updateAuditPayload.put("nameAr", saved.getNameAr());
        auditLogService.logAction("CATEGORY", saved.getId().longValue(), AuditAction.UPDATE, updateAuditPayload);

        return CategoryDto.fromEntity(saved);
    }

    @Transactional
    @Override
    public void deleteCategory(Integer categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        if (category.getChildren() != null && !category.getChildren().isEmpty()) {
            throw new IllegalArgumentException("Cannot delete category because it has subcategories");
        }
        if (category.getProducts() != null && !category.getProducts().isEmpty()) {
            throw new IllegalArgumentException("Cannot delete category because products are associated with it");
        }
        if (category.getStores() != null && !category.getStores().isEmpty()) {
            throw new IllegalArgumentException("Cannot delete category because stores are associated with it");
        }

        if (category.getImageUrl() != null) {
            fileStorageService.deleteFile(category.getImageUrl(), "categories");
        }

        java.util.Map<String, Object> deleteAuditPayload = new java.util.HashMap<>();
        deleteAuditPayload.put("nameEn", category.getNameEn());
        deleteAuditPayload.put("nameAr", category.getNameAr());

        categoryRepository.delete(category);

        auditLogService.logAction("CATEGORY", categoryId.longValue(), AuditAction.DELETE, deleteAuditPayload);
    }
}
