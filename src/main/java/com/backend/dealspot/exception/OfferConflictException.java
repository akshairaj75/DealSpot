package com.backend.dealspot.exception;

import com.backend.dealspot.entity.Offer;
import java.math.BigDecimal;
import java.time.LocalDate;

public class OfferConflictException extends RuntimeException {

    private final Long conflictingOfferId;
    private final String conflictingOfferTitle;
    private final BigDecimal offerPrice;
    private final BigDecimal originalPrice;
    private final LocalDate validFrom;
    private final LocalDate validUntil;
    private final Long productId;
    private final Integer storeId;
    private final Long specialOfferId;
    private final String specialOfferTitle;

    public OfferConflictException(String message, Offer conflictingOffer) {
        super(message);
        if (conflictingOffer != null) {
            this.conflictingOfferId = conflictingOffer.getId();
            this.conflictingOfferTitle = conflictingOffer.getTitleEn();
            this.offerPrice = conflictingOffer.getOfferPrice();
            this.originalPrice = conflictingOffer.getOriginalPrice();
            this.validFrom = conflictingOffer.getValidFrom();
            this.validUntil = conflictingOffer.getValidUntil();
            this.productId = conflictingOffer.getProduct() != null ? conflictingOffer.getProduct().getId() : null;
            this.storeId = conflictingOffer.getStore() != null ? conflictingOffer.getStore().getId() : null;
            this.specialOfferId = conflictingOffer.getSpecialOffer() != null ? conflictingOffer.getSpecialOffer().getId() : null;
            this.specialOfferTitle = conflictingOffer.getSpecialOffer() != null ? conflictingOffer.getSpecialOffer().getTitleEn() : null;
        } else {
            this.conflictingOfferId = null;
            this.conflictingOfferTitle = null;
            this.offerPrice = null;
            this.originalPrice = null;
            this.validFrom = null;
            this.validUntil = null;
            this.productId = null;
            this.storeId = null;
            this.specialOfferId = null;
            this.specialOfferTitle = null;
        }
    }

    public Long getConflictingOfferId() {
        return conflictingOfferId;
    }

    public String getConflictingOfferTitle() {
        return conflictingOfferTitle;
    }

    public BigDecimal getOfferPrice() {
        return offerPrice;
    }

    public BigDecimal getOriginalPrice() {
        return originalPrice;
    }

    public LocalDate getValidFrom() {
        return validFrom;
    }

    public LocalDate getValidUntil() {
        return validUntil;
    }

    public Long getProductId() {
        return productId;
    }

    public Integer getStoreId() {
        return storeId;
    }

    public Long getSpecialOfferId() {
        return specialOfferId;
    }

    public String getSpecialOfferTitle() {
        return specialOfferTitle;
    }
}
