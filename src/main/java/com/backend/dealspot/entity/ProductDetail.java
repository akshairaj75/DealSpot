package com.backend.dealspot.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "product_details")
public class ProductDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attribute_key_id", nullable = false)
    private AttributeKey attributeKey;

    @Column(name = "attr_value_en", nullable = false, length = 255)
    private String attrValueEn;

    @Column(name = "attr_value_ar", nullable = false, length = 255)
    private String attrValueAr;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder = 0;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public AttributeKey getAttributeKey() {
        return attributeKey;
    }

    public void setAttributeKey(AttributeKey attributeKey) {
        this.attributeKey = attributeKey;
    }

    public String getAttrValueEn() {
        return attrValueEn;
    }

    public void setAttrValueEn(String attrValueEn) {
        this.attrValueEn = attrValueEn;
    }

    public String getAttrValueAr() {
        return attrValueAr;
    }

    public void setAttrValueAr(String attrValueAr) {
        this.attrValueAr = attrValueAr;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
