package com.backend.dealspot.dto.city;

import java.math.BigDecimal;

import com.backend.dealspot.entity.City;

public class CityResponseDto {

    private Integer id;
    private String nameEn;
    private String nameAr;
    private String regionCode;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private boolean isActive;



    // Getters and Setters
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
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
    public String getRegionCode() {
        return regionCode;
    }
    public void setRegionCode(String regionCode) {
        this.regionCode = regionCode;
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
    public boolean isActive() {
        return isActive;
    }
    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

        public static CityResponseDto fromEntity(City city) {
        CityResponseDto dto = new CityResponseDto();
        dto.setId(city.getId());
        dto.setNameEn(city.getNameEn());
        dto.setNameAr(city.getNameAr());
        dto.setRegionCode(city.getRegionCode());
        dto.setLatitude(city.getLatitude());
        dto.setLongitude(city.getLongitude());
        dto.setActive(city.isActive());
        return dto;
    }
    
}
