package com.backend.dealspot.dto.attribute;

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

    AttributeKeyRegisterDto fromEntity(AttributeKey entity) {
        AttributeKeyRegisterDto dto = new AttributeKeyRegisterDto();
        dto.setAttrKeyEn(entity.getAttrKeyEn());
        dto.setAttrKeyAr(entity.getAttrKeyAr());
        return dto;
    }

}
