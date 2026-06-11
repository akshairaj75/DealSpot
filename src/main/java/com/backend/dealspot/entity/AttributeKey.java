package com.backend.dealspot.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "attributes_key")
public class AttributeKey extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "attr_key_en", nullable = false, length = 80)
    private String attrKeyEn;

    @Column(name = "attr_key_ar", nullable = false, length = 80)
    private String attrKeyAr;

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
}
