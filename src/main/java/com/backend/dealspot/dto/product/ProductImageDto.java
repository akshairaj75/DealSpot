package com.backend.dealspot.dto.product;

import com.backend.dealspot.entity.ProductImage;
import java.time.LocalDateTime;

public class ProductImageDto {

    private Long id;
    private Long productId;
    private String imageUrl;
    private String altTextEn;
    private String altTextAr;
    private Integer sortOrder;
    private boolean primary;
    private LocalDateTime createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getAltTextEn() {
        return altTextEn;
    }

    public void setAltTextEn(String altTextEn) {
        this.altTextEn = altTextEn;
    }

    public String getAltTextAr() {
        return altTextAr;
    }

    public void setAltTextAr(String altTextAr) {
        this.altTextAr = altTextAr;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public boolean isPrimary() {
        return primary;
    }

    public void setPrimary(boolean primary) {
        this.primary = primary;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public static ProductImageDto fromEntity(ProductImage entity) {
        if (entity == null) {
            return null;
        }
        ProductImageDto dto = new ProductImageDto();
        dto.setId(entity.getId());
        if (entity.getProduct() != null) {
            dto.setProductId(entity.getProduct().getId());
        }
        dto.setImageUrl(entity.getImageUrl());
        dto.setAltTextEn(entity.getAltTextEn());
        dto.setAltTextAr(entity.getAltTextAr());
        dto.setSortOrder(entity.getSortOrder());
        dto.setPrimary(entity.isPrimary());
        dto.setCreatedAt(entity.getCreatedAt());
        return dto;
    }
}
