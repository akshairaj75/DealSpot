package com.backend.dealspot.service.serviceImpl;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.backend.dealspot.dto.brand.BrandDto;
import com.backend.dealspot.dto.brand.BrandRegisterDto;
import com.backend.dealspot.entity.Brand;
import com.backend.dealspot.entity.Category;
import com.backend.dealspot.repository.BrandRepository;
import com.backend.dealspot.repository.CategoryRepository;
import com.backend.dealspot.service.BrandService;

@Service
public class BrandServiceImpl implements BrandService {

    private final BrandRepository brandRepository;
    private final CategoryRepository categoryRepository;
    private final FileStorageService fileStorageService;

    public BrandServiceImpl(BrandRepository brandRepository,
            CategoryRepository categoryRepository,
            FileStorageService fileStorageService) {
        this.brandRepository = brandRepository;
        this.categoryRepository = categoryRepository;
        this.fileStorageService = fileStorageService;
    }

    @Override
    @Transactional
    public BrandDto registerBrand(BrandRegisterDto dto, MultipartFile logoFile, MultipartFile bannerFile) {
        if (dto == null) {
            throw new IllegalArgumentException("Brand registration payload is required");
        }

        Brand brand = new Brand();
        brand.setNameEn(dto.getNameEn());
        brand.setNameAr(dto.getNameAr());
        brand.setDescriptionEn(dto.getDescriptionEn());
        brand.setDescriptionAr(dto.getDescriptionAr());
        brand.setWebsiteUrl(dto.getWebsiteUrl());
        brand.setFeatured(dto.isFeatured());
        brand.setActive(dto.isActive());

        // Handle Image Uploads
        try {
            if (logoFile != null && !logoFile.isEmpty()) {
                brand.setLogoUrl(fileStorageService.storeFile(logoFile, "brands/logos"));
            } else if (dto.getLogoUrl() != null) {
                brand.setLogoUrl(dto.getLogoUrl()); // fallback to URL if provided in JSON
            }

            if (bannerFile != null && !bannerFile.isEmpty()) {
                brand.setBannerUrl(fileStorageService.storeFile(bannerFile, "brands/banners"));
            } else if (dto.getBannerUrl() != null) {
                brand.setBannerUrl(dto.getBannerUrl());
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload brand assets", e);
        }

        // Map Categories
        if (dto.getCategoryIds() != null && !dto.getCategoryIds().isEmpty()) {
            // Category ID is Integer, convert from Long
            List<Integer> categoryIds = dto.getCategoryIds().stream()
                    .map(Long::intValue)
                    .toList();

            List<Category> categories = categoryRepository.findAllById(categoryIds);

            if (categories.isEmpty() || categories.size() != categoryIds.size()) {
                throw new IllegalArgumentException("One or more provided Category IDs do not exist in the database.");
            }

            brand.setCategories(categories);
        }

        Brand savedBrand = brandRepository.save(brand);
        return BrandDto.fromEntity(savedBrand);
    }

    @Override
    public List<BrandDto> fetchBrands() {

        List<Brand> result = brandRepository.findAll();

        return result.stream()
                .map(BrandDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public BrandDto getBrandById(Long id) {
        return BrandDto
                .fromEntity(brandRepository.findById(id).orElseThrow(() -> new RuntimeException("Brand not found")));
    }

    @Transactional
    @Override
    public BrandDto updateBrand(Long id, BrandRegisterDto dto, MultipartFile logoFile, MultipartFile bannerFile) {

        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Brand not found"));

        brand.setNameEn(dto.getNameEn());
        brand.setNameAr(dto.getNameAr());
        brand.setDescriptionEn(dto.getDescriptionEn());
        brand.setDescriptionAr(dto.getDescriptionAr());
        brand.setWebsiteUrl(dto.getWebsiteUrl());
        brand.setFeatured(dto.isFeatured());
        brand.setActive(dto.isActive());
        try {

            if (logoFile != null && !logoFile.isEmpty()) {
                if (brand.getLogoUrl() != null) {
                    fileStorageService.deleteFile(brand.getLogoUrl(), "brands/logos");
                }
                brand.setLogoUrl(fileStorageService.storeFile(logoFile, "brands/logos"));
            }

            if (bannerFile != null && !bannerFile.isEmpty()) {
                if (brand.getBannerUrl() != null) {
                    fileStorageService.deleteFile(brand.getBannerUrl(), "brands/banners");
                }
                if (bannerFile != null && !bannerFile.isEmpty()) {
                    brand.setBannerUrl(fileStorageService.storeFile(bannerFile, "brands/banners"));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload brand assets", e);
        }
        Brand savedBrand = brandRepository.save(brand);
        return BrandDto.fromEntity(savedBrand);
    }
}