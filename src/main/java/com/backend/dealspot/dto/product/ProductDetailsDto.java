package com.backend.dealspot.dto.product;
import com.backend.dealspot.entity.ProductDetail;
import java.time.LocalDateTime;


public class ProductDetailsDto {

    private Long id;
    private Long productId;
    private Long attributeKeyId;
    private String attrKeyAr;
    private String attrValueEn;
    private String attrValueAr;
    private Integer sortOrder;
    private LocalDateTime createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Long getAttributeKeyId() {
        return attributeKeyId;
    }

    public void setAttributeKeyId(Long attributeKeyId) {
        this.attributeKeyId = attributeKeyId;
    }

    public String getAttrKeyAr() {
        return attrKeyAr;
    }

    public void setAttrKeyAr(String attrKeyAr) {
        this.attrKeyAr = attrKeyAr;
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

    public static ProductDetailsDto fromEntity(ProductDetail entity) {
        if (entity == null) {
            return null;
        }
        ProductDetailsDto dto = new ProductDetailsDto();
        dto.setId(entity.getId());
        if (entity.getProduct() != null) {
            dto.setProductId(entity.getProduct().getId());
        }
        if (entity.getAttributeKey() != null) {
            dto.setAttributeKeyId(entity.getAttributeKey().getId());
        }
        dto.setAttrValueEn(entity.getAttrValueEn());
        dto.setAttrValueAr(entity.getAttrValueAr());
        dto.setSortOrder(entity.getSortOrder());
        dto.setCreatedAt(entity.getCreatedAt());
        return dto;
    }
}
