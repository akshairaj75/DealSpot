package com.backend.dealspot.dto.category;

public class CategoryOrderDto {

    private Integer id;
    private Integer sortOrder;

    public CategoryOrderDto() {
    }

    public CategoryOrderDto(Integer id, Integer sortOrder) {
        this.id = id;
        this.sortOrder = sortOrder;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }
}
