package com.backend.dealspot.dto.offer;

import java.math.BigDecimal;
import java.time.LocalDate;

public class OfferConflictResponseDto {
    private String error = "OFFER_CONFLICT";
    private String message;
    private Long conflictingOfferId;
    private String conflictingOfferTitle;
    private BigDecimal offerPrice;
    private BigDecimal originalPrice;
    private LocalDate validFrom;
    private LocalDate validUntil;
    private Long productId;
    private Integer storeId;
    private Long specialOfferId;
    private String specialOfferTitle;

    public OfferConflictResponseDto() {
    }

    public OfferConflictResponseDto(String message, Long conflictingOfferId, String conflictingOfferTitle,
                                    BigDecimal offerPrice, BigDecimal originalPrice, LocalDate validFrom,
                                    LocalDate validUntil, Long productId, Integer storeId,
                                    Long specialOfferId, String specialOfferTitle) {
        this.message = message;
        this.conflictingOfferId = conflictingOfferId;
        this.conflictingOfferTitle = conflictingOfferTitle;
        this.offerPrice = offerPrice;
        this.originalPrice = originalPrice;
        this.validFrom = validFrom;
        this.validUntil = validUntil;
        this.productId = productId;
        this.storeId = storeId;
        this.specialOfferId = specialOfferId;
        this.specialOfferTitle = specialOfferTitle;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getConflictingOfferId() {
        return conflictingOfferId;
    }

    public void setConflictingOfferId(Long conflictingOfferId) {
        this.conflictingOfferId = conflictingOfferId;
    }

    public String getConflictingOfferTitle() {
        return conflictingOfferTitle;
    }

    public void setConflictingOfferTitle(String conflictingOfferTitle) {
        this.conflictingOfferTitle = conflictingOfferTitle;
    }

    public BigDecimal getOfferPrice() {
        return offerPrice;
    }

    public void setOfferPrice(BigDecimal offerPrice) {
        this.offerPrice = offerPrice;
    }

    public BigDecimal getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(BigDecimal originalPrice) {
        this.originalPrice = originalPrice;
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

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Integer getStoreId() {
        return storeId;
    }

    public void setStoreId(Integer storeId) {
        this.storeId = storeId;
    }

    public Long getSpecialOfferId() {
        return specialOfferId;
    }

    public void setSpecialOfferId(Long specialOfferId) {
        this.specialOfferId = specialOfferId;
    }

    public String getSpecialOfferTitle() {
        return specialOfferTitle;
    }

    public void setSpecialOfferTitle(String specialOfferTitle) {
        this.specialOfferTitle = specialOfferTitle;
    }
}
