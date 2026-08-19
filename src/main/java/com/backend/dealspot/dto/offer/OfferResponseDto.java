package com.backend.dealspot.dto.offer;

import com.backend.dealspot.entity.Offer;
import com.backend.dealspot.enums.OfferBadgeType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class OfferResponseDto {

    private Long id;
    private Integer storeId;
    private String storeNameEn;
    private String storeNameAr;
    private String storeLogoUrl;
    private boolean storeVerified;
    private Integer categoryId;
    private String categoryNameEn;
    private String categoryNameAr;

    public String getStoreLogoUrl() {
        return storeLogoUrl;
    }

    public void setStoreLogoUrl(String storeLogoUrl) {
        this.storeLogoUrl = storeLogoUrl;
    }

    public boolean isStoreVerified() {
        return storeVerified;
    }

    public void setStoreVerified(boolean storeVerified) {
        this.storeVerified = storeVerified;
    }

    private Integer cityId;
    private String cityNameEn;
    private String cityNameAr;
    private Long productId;
    private String productNameEn;
    private String productNameAr;
    private String productPrimaryImageUrl;
    private String productImageUrl;
    private String titleEn;
    private String titleAr;
    private String descriptionEn;
    private String descriptionAr;
    private String termsEn;
    private String termsAr;
    private BigDecimal originalPrice;
    private BigDecimal offerPrice;
    private Integer discountPct;
    private OfferBadgeType badgeType;
    private String imageUrl;
    private String thumbnailUrl;
    private LocalDate validFrom;
    private LocalDate validUntil;
    private boolean featured;
    private boolean flash;
    private boolean online;
    private boolean inStore;
    private boolean active;
    private Long viewCount;
    private Integer saveCount;
    private Integer shareCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getStoreId() {
        return storeId;
    }

    public void setStoreId(Integer storeId) {
        this.storeId = storeId;
    }

    public String getStoreNameEn() {
        return storeNameEn;
    }

    public void setStoreNameEn(String storeNameEn) {
        this.storeNameEn = storeNameEn;
    }

    public String getStoreNameAr() {
        return storeNameAr;
    }

    public void setStoreNameAr(String storeNameAr) {
        this.storeNameAr = storeNameAr;
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

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductNameEn() {
        return productNameEn;
    }

    public void setProductNameEn(String productNameEn) {
        this.productNameEn = productNameEn;
    }

    public String getProductNameAr() {
        return productNameAr;
    }

    public void setProductNameAr(String productNameAr) {
        this.productNameAr = productNameAr;
    }

    public String getProductPrimaryImageUrl() {
        return productPrimaryImageUrl;
    }

    public void setProductPrimaryImageUrl(String productPrimaryImageUrl) {
        this.productPrimaryImageUrl = productPrimaryImageUrl;
    }

    public String getProductImageUrl() {
        return productImageUrl;
    }

    public void setProductImageUrl(String productImageUrl) {
        this.productImageUrl = productImageUrl;
    }

    public String getTitleEn() {
        return titleEn;
    }

    public void setTitleEn(String titleEn) {
        this.titleEn = titleEn;
    }

    public String getTitleAr() {
        return titleAr;
    }

    public void setTitleAr(String titleAr) {
        this.titleAr = titleAr;
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

    public String getTermsEn() {
        return termsEn;
    }

    public void setTermsEn(String termsEn) {
        this.termsEn = termsEn;
    }

    public String getTermsAr() {
        return termsAr;
    }

    public void setTermsAr(String termsAr) {
        this.termsAr = termsAr;
    }

    public BigDecimal getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(BigDecimal originalPrice) {
        this.originalPrice = originalPrice;
    }

    public BigDecimal getOfferPrice() {
        return offerPrice;
    }

    public void setOfferPrice(BigDecimal offerPrice) {
        this.offerPrice = offerPrice;
    }

    public Integer getDiscountPct() {
        return discountPct;
    }

    public void setDiscountPct(Integer discountPct) {
        this.discountPct = discountPct;
    }

    public OfferBadgeType getBadgeType() {
        return badgeType;
    }

    public void setBadgeType(OfferBadgeType badgeType) {
        this.badgeType = badgeType;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public LocalDate getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(LocalDate validFrom) {
        this.validFrom = validFrom;
    }

    public LocalDate getValidUntil() {
        return validUntil;
    }

    public void setValidUntil(LocalDate validUntil) {
        this.validUntil = validUntil;
    }

    public boolean isFeatured() {
        return featured;
    }

    public void setFeatured(boolean featured) {
        this.featured = featured;
    }

    public boolean isFlash() {
        return flash;
    }

    public void setFlash(boolean flash) {
        this.flash = flash;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public boolean isInStore() {
        return inStore;
    }

    public void setInStore(boolean inStore) {
        this.inStore = inStore;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Long getViewCount() {
        return viewCount;
    }

    public void setViewCount(Long viewCount) {
        this.viewCount = viewCount;
    }

    public Integer getSaveCount() {
        return saveCount;
    }

    public void setSaveCount(Integer saveCount) {
        this.saveCount = saveCount;
    }

    public Integer getShareCount() {
        return shareCount;
    }

    public void setShareCount(Integer shareCount) {
        this.shareCount = shareCount;
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

    public static OfferResponseDto fromEntity(Offer offer) {
        if (offer == null) {
            return null;
        }

        OfferResponseDto dto = new OfferResponseDto();
        dto.setId(offer.getId());
        dto.setTitleEn(offer.getTitleEn());
        dto.setTitleAr(offer.getTitleAr());
        dto.setDescriptionEn(offer.getDescriptionEn());
        dto.setDescriptionAr(offer.getDescriptionAr());
        dto.setTermsEn(offer.getTermsEn());
        dto.setTermsAr(offer.getTermsAr());
        dto.setOriginalPrice(offer.getOriginalPrice());
        dto.setOfferPrice(offer.getOfferPrice());
        dto.setDiscountPct(offer.getDiscountPct());
        dto.setBadgeType(offer.getBadgeType());
        dto.setImageUrl(offer.getImageUrl());
        dto.setThumbnailUrl(offer.getThumbnailUrl());
        dto.setValidFrom(offer.getValidFrom());
        dto.setValidUntil(offer.getValidUntil());
        dto.setFeatured(offer.isFeatured());
        dto.setFlash(offer.isFlash());
        dto.setOnline(offer.isOnline());
        dto.setInStore(offer.isInStore());
        dto.setActive(offer.isActive());
        dto.setViewCount(offer.getViewCount());
        dto.setSaveCount(offer.getSaveCount());
        dto.setShareCount(offer.getShareCount());
        dto.setCreatedAt(offer.getCreatedAt());
        dto.setUpdatedAt(offer.getUpdatedAt());

        if (offer.getStore() != null) {
            dto.setStoreId(offer.getStore().getId());
            dto.setStoreNameEn(offer.getStore().getNameEn());
            dto.setStoreNameAr(offer.getStore().getNameAr());
            dto.setStoreLogoUrl(offer.getStore().getLogoUrl());
            dto.setStoreVerified(offer.getStore().isVerified());
        }

        if (offer.getCategory() != null) {
            dto.setCategoryId(offer.getCategory().getId());
            dto.setCategoryNameEn(offer.getCategory().getNameEn());
            dto.setCategoryNameAr(offer.getCategory().getNameAr());
        }

        if (offer.getCity() != null) {
            dto.setCityId(offer.getCity().getId());
            dto.setCityNameEn(offer.getCity().getNameEn());
            dto.setCityNameAr(offer.getCity().getNameAr());
        }

        if (offer.getProduct() != null) {
            String prodImg = offer.getProduct().getPrimaryImageUrl();
            if ((prodImg == null || prodImg.trim().isEmpty()) && offer.getProduct().getImages() != null && !offer.getProduct().getImages().isEmpty()) {
                prodImg = offer.getProduct().getImages().get(0).getImageUrl();
            }

            dto.setProductId(offer.getProduct().getId());
            dto.setProductNameEn(offer.getProduct().getNameEn());
            dto.setProductNameAr(offer.getProduct().getNameAr());
            dto.setProductPrimaryImageUrl(prodImg);
            dto.setProductImageUrl(prodImg);

            // If offer does not have its own custom image, fall back directly to the product's primary image
            if (dto.getImageUrl() == null || dto.getImageUrl().trim().isEmpty()) {
                dto.setImageUrl(prodImg);
            }
            if (dto.getThumbnailUrl() == null || dto.getThumbnailUrl().trim().isEmpty()) {
                dto.setThumbnailUrl(prodImg);
            }
        }

        return dto;
    }
}
