package com.backend.dealspot.dto.attributeKey;

import com.backend.dealspot.entity.AttributeKey;

public class AttributeKeyRegisterDto {
    private String attrKeyEn;
    private String attrKeyAr;

    public String getAttrKeyEn() {
        return attrKeyEn;
    }

    public void setAttrKeyEn(String attrKeyEn) {
        this.attrKeyEn = attrKeyEn;
    }

    public String getAttrKeyAr() {
        return attrKeyAr;
    }

    public void setAttrKeyAr(String attrKeyAr) {
        this.attrKeyAr = attrKeyAr;
    }

    public static AttributeKeyRegisterDto fromEntity(AttributeKey entity) {
        AttributeKeyRegisterDto dto = new AttributeKeyRegisterDto();
        dto.setAttrKeyEn(entity.getAttrKeyEn());
        dto.setAttrKeyAr(entity.getAttrKeyAr());
        return dto;
    }

}
