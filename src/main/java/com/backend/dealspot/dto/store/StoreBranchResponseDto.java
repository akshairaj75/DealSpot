package com.backend.dealspot.dto.store;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import com.backend.dealspot.entity.StoreBranch;

public class StoreBranchResponseDto {
    private Integer id;
    private Integer storeId;
    private String storeNameEn;
    private String storeNameAr;
    private Integer cityId;
    private String cityNameEn;
    private String cityNameAr;
    private String branchName;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private LocalTime openTime;
    private LocalTime closeTime;
    private boolean twentyFourHours;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public LocalTime getOpenTime() {
        return openTime;
    }

    public void setOpenTime(LocalTime openTime) {
        this.openTime = openTime;
    }

    public LocalTime getCloseTime() {
        return closeTime;
    }

    public void setCloseTime(LocalTime closeTime) {
        this.closeTime = closeTime;
    }

    public boolean isTwentyFourHours() {
        return twentyFourHours;
    }

    public void setTwentyFourHours(boolean twentyFourHours) {
        this.twentyFourHours = twentyFourHours;
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

    public static StoreBranchResponseDto fromEntity(StoreBranch branch) {
        if (branch == null) {
            return null;
        }
        StoreBranchResponseDto dto = new StoreBranchResponseDto();
        dto.setId(branch.getId());
        dto.setBranchName(branch.getBranchName());
        dto.setLatitude(branch.getLatitude());
        dto.setLongitude(branch.getLongitude());
        dto.setOpenTime(branch.getOpenTime());
        dto.setCloseTime(branch.getCloseTime());
        dto.setTwentyFourHours(branch.isTwentyFourHours());
        dto.setActive(branch.isActive());
        dto.setCreatedAt(branch.getCreatedAt());
        dto.setUpdatedAt(branch.getUpdatedAt());

        if (branch.getStore() != null) {
            dto.setStoreId(branch.getStore().getId());
            dto.setStoreNameEn(branch.getStore().getNameEn());
            dto.setStoreNameAr(branch.getStore().getNameAr());
        }

        if (branch.getCity() != null) {
            dto.setCityId(branch.getCity().getId());
            dto.setCityNameEn(branch.getCity().getNameEn());
            dto.setCityNameAr(branch.getCity().getNameAr());
        }

        return dto;
    }
}
