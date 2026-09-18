package com.backend.dealspot.dto.offer;

import com.backend.dealspot.enums.OfferBadgeType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public class OfferPeriodSplitRequestDto {

    @NotNull(message = "Existing offer ID is required")
    private Long existingOfferId;

    private Long specialOfferId;

    @NotNull(message = "New offer price is required")
    @DecimalMin(value = "0.01", message = "Offer price must be greater than 0")
    private BigDecimal newOfferPrice;

    private BigDecimal newOriginalPrice;

    @NotNull(message = "Split start date is required")
    private LocalDate splitValidFrom;

    @NotNull(message = "Split end date is required")
    private LocalDate splitValidUntil;

    private String titleEn;
    private String titleAr;
    private OfferBadgeType badgeType;
    private Integer displayOrder = 0;

    public OfferPeriodSplitRequestDto() {
    }

    public Long getExistingOfferId() {
        return existingOfferId;
    }

    public void setExistingOfferId(Long existingOfferId) {
        this.existingOfferId = existingOfferId;
    }

    public Long getSpecialOfferId() {
        return specialOfferId;
    }

    public void setSpecialOfferId(Long specialOfferId) {
        this.specialOfferId = specialOfferId;
    }

    public BigDecimal getNewOfferPrice() {
        return newOfferPrice;
    }

    public void setNewOfferPrice(BigDecimal newOfferPrice) {
        this.newOfferPrice = newOfferPrice;
    }

    public BigDecimal getNewOriginalPrice() {
        return newOriginalPrice;
    }

    public void setNewOriginalPrice(BigDecimal newOriginalPrice) {
        this.newOriginalPrice = newOriginalPrice;
    }

    public LocalDate getSplitValidFrom() {
        return splitValidFrom;
    }

    public void setSplitValidFrom(LocalDate splitValidFrom) {
        this.splitValidFrom = splitValidFrom;
    }

    public LocalDate getSplitValidUntil() {
        return splitValidUntil;
    }

    public void setSplitValidUntil(LocalDate splitValidUntil) {
        this.splitValidUntil = splitValidUntil;
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

    public OfferBadgeType getBadgeType() {
        return badgeType;
    }

    public void setBadgeType(OfferBadgeType badgeType) {
        this.badgeType = badgeType;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }
}
