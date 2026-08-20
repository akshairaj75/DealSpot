package com.backend.dealspot.dto.partner;

import java.time.LocalDateTime;

import com.backend.dealspot.entity.PartnerRequest;
import com.backend.dealspot.enums.PartnerRequestStatus;

public class PartnerRequestResponseDto {

    private Long id;
    private String applicantName;
    private String applicantEmail;
    private String applicantPhone;
    private String storeNameEn;
    private String storeNameAr;
    private String descriptionEn;
    private String descriptionAr;
    private Integer cityId;
    private String cityNameEn;
    private String cityNameAr;
    private Integer categoryId;
    private String categoryNameEn;
    private String categoryNameAr;
    private String crNumber;
    private String vatNumber;
    private String website;
    private String logoUrl;
    private String bannerUrl;
    private String contactAddress;
    private PartnerRequestStatus status;
    private String rejectionReason;
    private Integer createdStoreId;
    private LocalDateTime reviewedAt;
    private LocalDateTime createdAt;

    public static PartnerRequestResponseDto fromEntity(PartnerRequest req) {
        PartnerRequestResponseDto dto = new PartnerRequestResponseDto();
        dto.setId(req.getId());
        dto.setApplicantName(req.getApplicantName());
        dto.setApplicantEmail(req.getApplicantEmail());
        dto.setApplicantPhone(req.getApplicantPhone());
        dto.setStoreNameEn(req.getStoreNameEn());
        dto.setStoreNameAr(req.getStoreNameAr());
        dto.setDescriptionEn(req.getDescriptionEn());
        dto.setDescriptionAr(req.getDescriptionAr());

        if (req.getCity() != null) {
            dto.setCityId(req.getCity().getId());
            dto.setCityNameEn(req.getCity().getNameEn());
            dto.setCityNameAr(req.getCity().getNameAr());
        }

        if (req.getCategory() != null) {
            dto.setCategoryId(req.getCategory().getId());
            dto.setCategoryNameEn(req.getCategory().getNameEn());
            dto.setCategoryNameAr(req.getCategory().getNameAr());
        }

        dto.setCrNumber(req.getCrNumber());
        dto.setVatNumber(req.getVatNumber());
        dto.setWebsite(req.getWebsite());
        dto.setLogoUrl(req.getLogoUrl());
        dto.setBannerUrl(req.getBannerUrl());
        dto.setContactAddress(req.getContactAddress());
        dto.setStatus(req.getStatus());
        dto.setRejectionReason(req.getRejectionReason());
        if (req.getCreatedStore() != null) {
            dto.setCreatedStoreId(req.getCreatedStore().getId());
        }
        dto.setReviewedAt(req.getReviewedAt());
        dto.setCreatedAt(req.getCreatedAt());
        return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getApplicantName() {
        return applicantName;
    }

    public void setApplicantName(String applicantName) {
        this.applicantName = applicantName;
    }

    public String getApplicantEmail() {
        return applicantEmail;
    }

    public void setApplicantEmail(String applicantEmail) {
        this.applicantEmail = applicantEmail;
    }

    public String getApplicantPhone() {
        return applicantPhone;
    }

    public void setApplicantPhone(String applicantPhone) {
        this.applicantPhone = applicantPhone;
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

    public String getCrNumber() {
        return crNumber;
    }

    public void setCrNumber(String crNumber) {
        this.crNumber = crNumber;
    }

    public String getVatNumber() {
        return vatNumber;
    }

    public void setVatNumber(String vatNumber) {
        this.vatNumber = vatNumber;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
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

    public String getContactAddress() {
        return contactAddress;
    }

    public void setContactAddress(String contactAddress) {
        this.contactAddress = contactAddress;
    }

    public PartnerRequestStatus getStatus() {
        return status;
    }

    public void setStatus(PartnerRequestStatus status) {
        this.status = status;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public Integer getCreatedStoreId() {
        return createdStoreId;
    }

    public void setCreatedStoreId(Integer createdStoreId) {
        this.createdStoreId = createdStoreId;
    }

    public LocalDateTime getReviewedAt() {
        return reviewedAt;
    }

    public void setReviewedAt(LocalDateTime reviewedAt) {
        this.reviewedAt = reviewedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
