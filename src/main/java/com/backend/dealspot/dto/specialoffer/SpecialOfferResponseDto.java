package com.backend.dealspot.dto.specialoffer;

import com.backend.dealspot.dto.offer.OfferResponseDto;
import com.backend.dealspot.entity.SpecialOffer;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SpecialOfferResponseDto {

    private Long id;
    private String titleEn;
    private String titleAr;
    private String descriptionEn;
    private String descriptionAr;
    private String bannerUrl;
    private String thumbnailUrl;

    private Integer storeId;
    private String storeNameEn;
    private String storeNameAr;
    private String storeLogoUrl;

    private Integer cityId;
    private String cityNameEn;
    private String cityNameAr;

    private String badgeText;
    private LocalDate validFrom;
    private LocalDate validUntil;
    private boolean featured;
    private boolean active;

    private boolean expired;
    private boolean upcoming;
    private String status;
    private Long daysRemaining;

    private Long viewCount;
    private int totalOffersCount;
    private List<OfferResponseDto> offers = new ArrayList<>();

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getBannerUrl() {
        return bannerUrl;
    }

    public void setBannerUrl(String bannerUrl) {
        this.bannerUrl = bannerUrl;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
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

    public String getStoreLogoUrl() {
        return storeLogoUrl;
    }

    public void setStoreLogoUrl(String storeLogoUrl) {
        this.storeLogoUrl = storeLogoUrl;
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

    public String getBadgeText() {
        return badgeText;
    }

    public void setBadgeText(String badgeText) {
        this.badgeText = badgeText;
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

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isExpired() {
        return expired;
    }

    public void setExpired(boolean expired) {
        this.expired = expired;
    }

    public boolean isUpcoming() {
        return upcoming;
    }

    public void setUpcoming(boolean upcoming) {
        this.upcoming = upcoming;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getDaysRemaining() {
        return daysRemaining;
    }

    public void setDaysRemaining(Long daysRemaining) {
        this.daysRemaining = daysRemaining;
    }

    public Long getViewCount() {
        return viewCount;
    }

    public void setViewCount(Long viewCount) {
        this.viewCount = viewCount;
    }

    public int getTotalOffersCount() {
        return totalOffersCount;
    }

    public void setTotalOffersCount(int totalOffersCount) {
        this.totalOffersCount = totalOffersCount;
    }

    public List<OfferResponseDto> getOffers() {
        return offers;
    }

    public void setOffers(List<OfferResponseDto> offers) {
        this.offers = offers;
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

    public static SpecialOfferResponseDto fromEntity(SpecialOffer specialOffer, boolean includeOffers) {
        if (specialOffer == null) {
            return null;
        }

        SpecialOfferResponseDto dto = new SpecialOfferResponseDto();
        dto.setId(specialOffer.getId());
        dto.setTitleEn(specialOffer.getTitleEn());
        dto.setTitleAr(specialOffer.getTitleAr());
        dto.setDescriptionEn(specialOffer.getDescriptionEn());
        dto.setDescriptionAr(specialOffer.getDescriptionAr());
        dto.setBannerUrl(specialOffer.getBannerUrl());
        dto.setThumbnailUrl(specialOffer.getThumbnailUrl());
        dto.setBadgeText(specialOffer.getBadgeText());
        dto.setValidFrom(specialOffer.getValidFrom());
        dto.setValidUntil(specialOffer.getValidUntil());
        dto.setFeatured(specialOffer.isFeatured());
        dto.setActive(specialOffer.isActive());
        dto.setViewCount(specialOffer.getViewCount() != null ? specialOffer.getViewCount() : 0L);
        dto.setCreatedAt(specialOffer.getCreatedAt());
        dto.setUpdatedAt(specialOffer.getUpdatedAt());

        LocalDate today = LocalDate.now();
        boolean isExp = specialOffer.getValidUntil() != null && specialOffer.getValidUntil().isBefore(today);
        boolean isUpc = specialOffer.getValidFrom() != null && specialOffer.getValidFrom().isAfter(today);

        dto.setExpired(isExp);
        dto.setUpcoming(isUpc);

        if (!specialOffer.isActive()) {
            dto.setStatus("DISABLED");
        } else if (isExp) {
            dto.setStatus("EXPIRED");
        } else if (isUpc) {
            dto.setStatus("UPCOMING");
        } else {
            dto.setStatus("ACTIVE");
        }

        if (specialOffer.getValidUntil() != null && !isExp) {
            dto.setDaysRemaining(Math.max(0, ChronoUnit.DAYS.between(today, specialOffer.getValidUntil())));
        } else {
            dto.setDaysRemaining(0L);
        }

        if (specialOffer.getStore() != null) {
            dto.setStoreId(specialOffer.getStore().getId());
            dto.setStoreNameEn(specialOffer.getStore().getNameEn());
            dto.setStoreNameAr(specialOffer.getStore().getNameAr());
            dto.setStoreLogoUrl(specialOffer.getStore().getLogoUrl());
        }

        if (specialOffer.getCity() != null) {
            dto.setCityId(specialOffer.getCity().getId());
            dto.setCityNameEn(specialOffer.getCity().getNameEn());
            dto.setCityNameAr(specialOffer.getCity().getNameAr());
        }

        if (specialOffer.getOffers() != null) {
            dto.setTotalOffersCount(specialOffer.getOffers().size());
            if (includeOffers) {
                dto.setOffers(specialOffer.getOffers().stream()
                        .map(OfferResponseDto::fromEntity)
                        .collect(Collectors.toList()));
            }
        } else {
            dto.setTotalOffersCount(0);
        }

        return dto;
    }
}
