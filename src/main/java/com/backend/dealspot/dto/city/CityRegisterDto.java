package com.backend.dealspot.dto.city;

import com.backend.dealspot.entity.City;

public class CityRegisterDto {
    private String nameEn;
    private String nameAr;
    private String regionCode;
    private String latitude;
    private String longitude;
    private boolean isActive;

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
    public String getLatitude() {
        return latitude;
    }
    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }
    public String getLongitude() {
        return longitude;
    }
    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
    public boolean isActive() {
        return isActive;
    }
    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    public static CityRegisterDto fromEntity(City city) {
        CityRegisterDto dto = new CityRegisterDto();
        dto.setNameEn(city.getNameEn());
        dto.setNameAr(city.getNameAr());
        dto.setRegionCode(city.getRegionCode());
        dto.setLatitude(city.getLatitude().toString());
        dto.setLongitude(city.getLongitude().toString());
        dto.setActive(city.isActive());
        return dto;
    }

}
