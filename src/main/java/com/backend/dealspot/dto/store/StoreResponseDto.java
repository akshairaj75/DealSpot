package com.backend.dealspot.dto.store;

import java.time.LocalDateTime;
import com.backend.dealspot.entity.Store;

public class StoreResponseDto {
    private Integer id;
    private Integer cityId;
    private String cityNameEn;
    private String cityNameAr;
    private Integer categoryId;
    private String categoryNameEn;
    private String categoryNameAr;
    private String nameEn;
    private String nameAr;
    private String descriptionEn;
    private String descriptionAr;
    private String logoUrl;
    private String bannerUrl;
    private String vatNumber;
    private String crNumber;
    private boolean verified;
    private boolean featured;
    private boolean active;
    private long followersCount;
    private boolean isFollowed;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public long getFollowersCount() {
        return followersCount;
    }

    public void setFollowersCount(long followersCount) {
        this.followersCount = followersCount;
    }

    public boolean isFollowed() {
        return isFollowed;
    }

    public void setFollowed(boolean isFollowed) {
        this.isFollowed = isFollowed;
    }

    public Integer getId() {
        return id;
    }


    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCityId() {
        return cityId;
    }

    public void setCityId(Integer cityId) {
        this.cityId = cityId;
    }

    public String getCityNameEn() {
        return cityNameEn;
    }

    public void setCityNameEn(String cityNameEn) {
        this.cityNameEn = cityNameEn;
    }

    public String getCityNameAr() {
        return cityNameAr;
    }

    public void setCityNameAr(String cityNameAr) {
        this.cityNameAr = cityNameAr;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryNameEn() {
        return categoryNameEn;
    }

    public void setCategoryNameEn(String categoryNameEn) {
        this.categoryNameEn = categoryNameEn;
    }

    public String getCategoryNameAr() {
        return categoryNameAr;
    }

    public void setCategoryNameAr(String categoryNameAr) {
        this.categoryNameAr = categoryNameAr;
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

    public String getVatNumber() {
        return vatNumber;
    }

    public void setVatNumber(String vatNumber) {
        this.vatNumber = vatNumber;
    }

    public String getCrNumber() {
        return crNumber;
    }

    public void setCrNumber(String crNumber) {
        this.crNumber = crNumber;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public static StoreResponseDto fromEntity(Store store) {
        if (store == null) {
            return null;
        }
        StoreResponseDto dto = new StoreResponseDto();
        dto.setId(store.getId());
        dto.setNameEn(store.getNameEn());
        dto.setNameAr(store.getNameAr());
        dto.setDescriptionEn(store.getDescriptionEn());
        dto.setDescriptionAr(store.getDescriptionAr());
        dto.setLogoUrl(store.getLogoUrl());
        dto.setBannerUrl(store.getBannerUrl());
        dto.setVatNumber(store.getVatNumber());
        dto.setCrNumber(store.getCrNumber());
        dto.setVerified(store.isVerified());
        dto.setFeatured(store.isFeatured());
        dto.setActive(store.isActive());
        dto.setCreatedAt(store.getCreatedAt());
        dto.setUpdatedAt(store.getUpdatedAt());

        if (store.getCity() != null) {
            dto.setCityId(store.getCity().getId());
            dto.setCityNameEn(store.getCity().getNameEn());
            dto.setCityNameAr(store.getCity().getNameAr());
        }

        if (store.getCategory() != null) {
            dto.setCategoryId(store.getCategory().getId());
            dto.setCategoryNameEn(store.getCategory().getNameEn());
            dto.setCategoryNameAr(store.getCategory().getNameAr());
        }

        if (store.getFollowers() != null) {
            dto.setFollowersCount(store.getFollowers().size());
        }

        return dto;
    }
}

