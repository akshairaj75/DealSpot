package com.backend.dealspot.dto.brand;

import com.backend.dealspot.entity.Brand;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BrandDto {
    
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
    private List<Integer> categoryIds = new ArrayList<>();

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

    public List<Integer> getCategoryIds() {
        return categoryIds;
    }

    public void setCategoryIds(List<Integer> categoryIds) {
        this.categoryIds = categoryIds;
    }

    public static BrandDto fromEntity(Brand brand) {
        if (brand == null) return null;
        
        BrandDto dto = new BrandDto();
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
            dto.setCategoryIds(brand.getCategories().stream()
                    .map(category -> category.getId())
                    .collect(Collectors.toList()));
        }
        
        return dto;
    }
}