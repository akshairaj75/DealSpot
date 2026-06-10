package com.backend.dealspot.dto.category;

import com.backend.dealspot.entity.Category;

public class CategoryDto {

    private Integer id;
    private Integer parentId; // Use null if it's a top-level category
    private String nameEn;
    private String nameAr;
    private String iconSlug;
    private String imageUrl;
    private Integer sortOrder;
    private Boolean active;

    public Integer getId() {
        return id;
    }

    public Integer getParentId() {
        return parentId;
    }

    public String getNameEn() {
        return nameEn;
    }

    public String getNameAr() {
        return nameAr;
    }

    public String getIconSlug() {
        return iconSlug;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public Boolean getActive() {
        return active;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public void setNameEn(String nameEn) {
        this.nameEn = nameEn;
    }

    public void setNameAr(String nameAr) {
        this.nameAr = nameAr;
    }

    public void setIconSlug(String iconSlug) {
        this.iconSlug = iconSlug;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public static CategoryDto fromEntity(Category cat) {
        CategoryDto dto = new CategoryDto();
        dto.setId(cat.getId());
        dto.setParentId(cat.getParent() != null ? cat.getParent().getId() : null);
        dto.setNameEn(cat.getNameEn());
        dto.setNameAr(cat.getNameAr());
        dto.setActive(cat.isActive());
        dto.setIconSlug(cat.getIconSlug());
        dto.setImageUrl(cat.getImageUrl());
        dto.setSortOrder(cat.getSortOrder());
        return dto;
    }
}
