package com.backend.dealspot.dto.product;

import com.backend.dealspot.entity.Product;
import com.backend.dealspot.enums.ProductUnit;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class ProductResponseDto {

    private Long id;
    private Long categoryId;
    private Long categoryParentId;

    private String brand;
    private String brandNameEn;
    private String brandNameAr;
    private String brandImage;
    private Long brandId;
    private String sku;
    private String barcode;
    private String nameEn;
    private String nameAr;
    private String descriptionEn;
    private String descriptionAr;
    private String primaryImageUrl;
    private ProductUnit unit;
    private BigDecimal unitSize;
    private boolean active;
    private List<ProductDetailsDto> details;
    private List<ProductImageDto> images;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public List<ProductDetailsDto> getDetails() {
        return details;
    }

    public void setDetails(List<ProductDetailsDto> details) {
        this.details = details;
    }

    public List<ProductImageDto> getImages() {
        return images;
    }

    public void setImages(List<ProductImageDto> images) {
        this.images = images;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public Long getCategoryParentId() {
        return categoryParentId;
    }

    public void setCategoryParentId(Long categoryParentId) {
        this.categoryParentId = categoryParentId;
    }

    public String getBrandNameEn() {
        return brandNameEn;
    }

    public void setBrandNameEn(String brandNameEn) {
        this.brandNameEn = brandNameEn;
    }

    public String getBrandNameAr() {
        return brandNameAr;
    }

    public void setBrandNameAr(String brandNameAr) {
        this.brandNameAr = brandNameAr;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getBrandImage() {
        return brandImage;
    }

    public void setBrandImage(String brandImage) {
        this.brandImage = brandImage;
    }

    public Long getBrandId() {
        return brandId;
    }

    public void setBrandId(Long brandId) {
        this.brandId = brandId;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
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

    public String getPrimaryImageUrl() {
        return primaryImageUrl;
    }

    public void setPrimaryImageUrl(String primaryImageUrl) {
        this.primaryImageUrl = primaryImageUrl;
    }

    public ProductUnit getUnit() {
        return unit;
    }

    public void setUnit(ProductUnit unit) {
        this.unit = unit;
    }

    public BigDecimal getUnitSize() {
        return unitSize;
    }

    public void setUnitSize(BigDecimal unitSize) {
        this.unitSize = unitSize;
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

    public static ProductResponseDto fromEntity(Product product) {
        ProductResponseDto responseDto = new ProductResponseDto();
        responseDto.setId(product.getId());
        if (product.getCategory() != null) {
            responseDto.setCategoryId(product.getCategory().getId().longValue());
        }
        if (product.getBrand() != null) {
            responseDto.setBrandId(product.getBrand().getId());
            responseDto.setBrandNameEn(product.getBrand().getNameEn());
            responseDto.setBrandNameAr(product.getBrand().getNameAr());
            responseDto.setBrand(product.getBrand().getNameEn() != null ? product.getBrand().getNameEn() : product.getBrand().getNameAr());
            responseDto.setBrandImage(product.getBrand().getLogoUrl());
        }
        responseDto.setSku(product.getSku());
        responseDto.setBarcode(product.getBarcode());
        responseDto.setNameEn(product.getNameEn());
        responseDto.setNameAr(product.getNameAr());
        responseDto.setDescriptionEn(product.getDescriptionEn());
        String primaryImg = product.getPrimaryImageUrl();
        if ((primaryImg == null || primaryImg.trim().isEmpty()) && product.getImages() != null && !product.getImages().isEmpty()) {
            primaryImg = product.getImages().get(0).getImageUrl();
        }
        responseDto.setPrimaryImageUrl(primaryImg);
        responseDto.setUnit(product.getUnit());
        responseDto.setUnitSize(product.getUnitSize());
        responseDto.setActive(product.isActive());

        if (product.getDetails() != null) {
            responseDto.setDetails(product.getDetails().stream()
                    .map(ProductDetailsDto::fromEntity)
                    .collect(Collectors.toList()));
        }

        if (product.getImages() != null) {
            responseDto.setImages(product.getImages().stream()
                    .map(ProductImageDto::fromEntity)
                    .collect(Collectors.toList()));
        }
        

        // if (product.getCategory() != null) {
        //     responseDto.setCategoryId(product.getCategory().getId().longValue());
        //     CategoryRepository rep.findById(product.getCategory().getId())
        //             .ifPresent(category -> {
        //                 if (category.getParentCategory() != null) {
        //                     responseDto.setCategoryParentId(category.getParentCategory().getId().longValue());
        //                 }
        //             });
        //         }
        responseDto.setCreatedAt(product.getCreatedAt());
        responseDto.setUpdatedAt(product.getUpdatedAt());
        return responseDto;
    }
}
