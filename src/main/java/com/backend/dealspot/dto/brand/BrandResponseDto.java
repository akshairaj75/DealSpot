package com.backend.dealspot.dto.brand;

import com.backend.dealspot.dto.category.CategoryDto;
import com.backend.dealspot.entity.Brand;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BrandResponseDto {

    private Long id;
    private String nameEn;
    private String nameAr;
    private String descriptionEn;
    private String descriptionAr;
    private String logoUrl;
    private String bannerUrl;
    private String websiteUrl;
    private boolean featured;
    private boolean active;
    private List<CategoryDto> categories = new ArrayList<>();

    public List<CategoryDto> getCategories() {
        return categories;
    }

    public void setCategories(List<CategoryDto> categories) {
        this.categories = categories;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNameEn() {
        return nameEn;
    }

    public void setNameEn(String nameEn) {
        this.nameEn = nameEn;
    }

    public String getNameAr() {
        return nameAr;
    }

    public void setNameAr(String nameAr) {
        this.nameAr = nameAr;
    }

    public String getDescriptionEn() {
        return descriptionEn;
    }

    public void setDescriptionEn(String descriptionEn) {
        this.descriptionEn = descriptionEn;
    }

    public String getDescriptionAr() {
        return descriptionAr;
    }

    public void setDescriptionAr(String descriptionAr) {
        this.descriptionAr = descriptionAr;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public String getBannerUrl() {
        return bannerUrl;
    }

    public void setBannerUrl(String bannerUrl) {
        this.bannerUrl = bannerUrl;
    }

    public String getWebsiteUrl() {
        return websiteUrl;
    }

    public void setWebsiteUrl(String websiteUrl) {
        this.websiteUrl = websiteUrl;
    }

    public boolean isFeatured() {
        return featured;
    }

    public void setFeatured(boolean featured) {
        this.featured = featured;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public static BrandResponseDto fromEntity(Brand brand) {
        if (brand == null)
            return null;

        BrandResponseDto dto = new BrandResponseDto();
        dto.setId(brand.getId());
        dto.setNameEn(brand.getNameEn());
        dto.setNameAr(brand.getNameAr());
        dto.setDescriptionEn(brand.getDescriptionEn());
        dto.setDescriptionAr(brand.getDescriptionAr());
        dto.setLogoUrl(brand.getLogoUrl());
        dto.setBannerUrl(brand.getBannerUrl());
        dto.setWebsiteUrl(brand.getWebsiteUrl());
        dto.setFeatured(brand.isFeatured());
        dto.setActive(brand.isActive());

        if (brand.getCategories() != null) {
            dto.setCategories(brand.getCategories().stream()
                    .map(CategoryDto::fromEntity)
                    .collect(Collectors.toList()));
        }

        return dto;
    }
}
