package com.backend.dealspot.dto.attributeKey;
import com.backend.dealspot.entity.AttributeKey;

public class AttributeKeyDto {
    public Long id;
    public String attrKeyEn;
    public String attrKeyAr;
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public static AttributeKeyDto fromEntity(AttributeKey entity) {
        if (entity == null) {
            return null;
        }
        AttributeKeyDto dto = new AttributeKeyDto();
        dto.setId(entity.getId());
        dto.setAttrKeyEn(entity.getAttrKeyEn());
        dto.setAttrKeyAr(entity.getAttrKeyAr());
        return dto;
    }
    


}
