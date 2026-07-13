package com.backend.dealspot.dto.coupon;

import com.backend.dealspot.entity.CouponCode;
import com.backend.dealspot.enums.DiscountType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class CouponCodeResponseDto {

    private Long id;
    private Long offerId;
    private String offerTitleEn;
    private String offerTitleAr;
    private Integer storeId;
    private String storeNameEn;
    private String storeNameAr;
    private Long productId;
    private String productNameEn;
    private String productNameAr;
    private String code;
    private Integer maxUses;
    private Integer usedCount;
    private DiscountType discountType;
    private BigDecimal discountValue;
    private BigDecimal minCartValue;
    private LocalDate validFrom;
    private LocalDate validUntil;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOfferId() {
        return offerId;
    }

    public void setOfferId(Long offerId) {
        this.offerId = offerId;
    }

    public String getOfferTitleEn() {
        return offerTitleEn;
    }

    public void setOfferTitleEn(String offerTitleEn) {
        this.offerTitleEn = offerTitleEn;
    }

    public String getOfferTitleAr() {
        return offerTitleAr;
    }

    public void setOfferTitleAr(String offerTitleAr) {
        this.offerTitleAr = offerTitleAr;
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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Integer getMaxUses() {
        return maxUses;
    }

    public void setMaxUses(Integer maxUses) {
        this.maxUses = maxUses;
    }

    public Integer getUsedCount() {
        return usedCount;
    }

    public void setUsedCount(Integer usedCount) {
        this.usedCount = usedCount;
    }

    public DiscountType getDiscountType() {
        return discountType;
    }

    public void setDiscountType(DiscountType discountType) {
        this.discountType = discountType;
    }

    public BigDecimal getDiscountValue() {
        return discountValue;
    }

    public void setDiscountValue(BigDecimal discountValue) {
        this.discountValue = discountValue;
    }

    public BigDecimal getMinCartValue() {
        return minCartValue;
    }

    public void setMinCartValue(BigDecimal minCartValue) {
        this.minCartValue = minCartValue;
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

    public static CouponCodeResponseDto fromEntity(CouponCode couponCode) {
        if (couponCode == null) {
            return null;
        }

        CouponCodeResponseDto dto = new CouponCodeResponseDto();
        dto.setId(couponCode.getId());
        dto.setCode(couponCode.getCode());
        dto.setMaxUses(couponCode.getMaxUses());
        dto.setUsedCount(couponCode.getUsedCount());
        dto.setDiscountType(couponCode.getDiscountType());
        dto.setDiscountValue(couponCode.getDiscountValue());
        dto.setMinCartValue(couponCode.getMinCartValue());
        dto.setValidFrom(couponCode.getValidFrom());
        dto.setValidUntil(couponCode.getValidUntil());
        dto.setActive(couponCode.isActive());
        dto.setCreatedAt(couponCode.getCreatedAt());
        dto.setUpdatedAt(couponCode.getUpdatedAt());

        if (couponCode.getOffer() != null) {
            dto.setOfferId(couponCode.getOffer().getId());
            dto.setOfferTitleEn(couponCode.getOffer().getTitleEn());
            dto.setOfferTitleAr(couponCode.getOffer().getTitleAr());
        }

        if (couponCode.getStore() != null) {
            dto.setStoreId(couponCode.getStore().getId());
            dto.setStoreNameEn(couponCode.getStore().getNameEn());
            dto.setStoreNameAr(couponCode.getStore().getNameAr());
        }

        if (couponCode.getProduct() != null) {
            dto.setProductId(couponCode.getProduct().getId());
            dto.setProductNameEn(couponCode.getProduct().getNameEn());
            dto.setProductNameAr(couponCode.getProduct().getNameAr());
        }

        return dto;
    }
}
