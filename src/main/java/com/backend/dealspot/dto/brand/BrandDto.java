package com.backend.dealspot.dto.brand;

import com.backend.dealspot.entity.Brand;

public class BrandDto {

    private Long brandId;
    private String name;

    public BrandDto() {
    }

    public BrandDto(Long brandId, String name) {
        this.brandId = brandId;
        this.name = name;
    }

    public Long getBrandId() {
        return brandId;
    }

    public void setBrandId(Long brandId) {
        this.brandId = brandId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static BrandDto fromEntity(Brand brand) {
        if (brand == null) {
            return null;
        }
        return new BrandDto(brand.getId(), brand.getNameEn());
    }
}