package com.backend.dealspot.dto.flyer;

import com.backend.dealspot.entity.Flyer;
import com.backend.dealspot.entity.FlyerPage;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FlyerResponseDto {

    private Integer id;
    private Integer storeId;
    private String storeNameEn;
    private String storeNameAr;
    private String storeLogoUrl;
    private boolean storeVerified;
    private Integer cityId;

    public String getStoreLogoUrl() {
        return storeLogoUrl;
    }

    public void setStoreLogoUrl(String storeLogoUrl) {
        this.storeLogoUrl = storeLogoUrl;
    }

    public boolean isStoreVerified() {
        return storeVerified;
    }

    public void setStoreVerified(boolean storeVerified) {
        this.storeVerified = storeVerified;
    }
    private String cityNameEn;
    private String cityNameAr;
    private String titleEn;
    private String titleAr;
    private String descriptionEn;
    private String descriptionAr;
    private String coverImageUrl;
    private String pdfUrl;
    private Integer totalPages;
    private LocalDate validFrom;
    private LocalDate validUntil;
    private boolean active;
    private Long viewCount;
    private List<FlyerPageResponseDto> pages = new ArrayList<>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getStoreId() {
        return storeId;
    }

    public void setStoreId(Integer storeId) {
        this.storeId = storeId;
    }

    public String getStoreNameEn() {
        return storeNameEn;
    }

    public void setStoreNameEn(String storeNameEn) {
        this.storeNameEn = storeNameEn;
    }

    public String getStoreNameAr() {
        return storeNameAr;
    }

    public void setStoreNameAr(String storeNameAr) {
        this.storeNameAr = storeNameAr;
    }

    public Integer getCityId() {
        return cityId;
    }

    public void setCityId(Integer cityId) {
        this.cityId = cityId;
    }

    public String getCityNameEn() {
        return cityNameEn;
    }

    public void setCityNameEn(String cityNameEn) {
        this.cityNameEn = cityNameEn;
    }

    public String getCityNameAr() {
        return cityNameAr;
    }

    public void setCityNameAr(String cityNameAr) {
        this.cityNameAr = cityNameAr;
    }

    public String getTitleEn() {
        return titleEn;
    }

    public void setTitleEn(String titleEn) {
        this.titleEn = titleEn;
    }

    public String getTitleAr() {
        return titleAr;
    }

    public void setTitleAr(String titleAr) {
        this.titleAr = titleAr;
    }

    public String getDescriptionEn() {
        return descriptionEn;
    }

    public void setDescriptionEn(String descriptionEn) {
        this.descriptionEn = descriptionEn;
    }

    public String getDescriptionAr() {
        return descriptionAr;
    }

    public void setDescriptionAr(String descriptionAr) {
        this.descriptionAr = descriptionAr;
    }

    public String getCoverImageUrl() {
        return coverImageUrl;
    }

    public void setCoverImageUrl(String coverImageUrl) {
        this.coverImageUrl = coverImageUrl;
    }

    public String getPdfUrl() {
        return pdfUrl;
    }

    public void setPdfUrl(String pdfUrl) {
        this.pdfUrl = pdfUrl;
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }

    public LocalDate getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(LocalDate validFrom) {
        this.validFrom = validFrom;
    }

    public LocalDate getValidUntil() {
        return validUntil;
    }

    public void setValidUntil(LocalDate validUntil) {
        this.validUntil = validUntil;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Long getViewCount() {
        return viewCount;
    }

    public void setViewCount(Long viewCount) {
        this.viewCount = viewCount;
    }

    public List<FlyerPageResponseDto> getPages() {
        return pages;
    }

    public void setPages(List<FlyerPageResponseDto> pages) {
        this.pages = pages;
    }

    public static FlyerResponseDto fromEntity(Flyer flyer) {
        if (flyer == null) {
            return null;
        }

        FlyerResponseDto dto = new FlyerResponseDto();
        dto.setId(flyer.getId());
        dto.setTitleEn(flyer.getTitleEn());
        dto.setTitleAr(flyer.getTitleAr());
        dto.setDescriptionEn(flyer.getDescriptionEn());
        dto.setDescriptionAr(flyer.getDescriptionAr());
        dto.setCoverImageUrl(flyer.getCoverImageUrl());
        dto.setPdfUrl(flyer.getPdfUrl());
        dto.setTotalPages(flyer.getTotalPages());
        dto.setValidFrom(flyer.getValidFrom());
        dto.setValidUntil(flyer.getValidUntil());
        dto.setActive(flyer.isActive());
        dto.setViewCount(flyer.getViewCount());

        if (flyer.getStore() != null) {
            dto.setStoreId(flyer.getStore().getId());
            dto.setStoreNameEn(flyer.getStore().getNameEn());
            dto.setStoreNameAr(flyer.getStore().getNameAr());
            dto.setStoreLogoUrl(flyer.getStore().getLogoUrl());
            dto.setStoreVerified(flyer.getStore().isVerified());
        }

        if (flyer.getCity() != null) {
            dto.setCityId(flyer.getCity().getId());
            dto.setCityNameEn(flyer.getCity().getNameEn());
            dto.setCityNameAr(flyer.getCity().getNameAr());
        }

        if (flyer.getPages() != null) {
            dto.setPages(flyer.getPages().stream()
                .map(FlyerPageResponseDto::fromEntity)
                .collect(Collectors.toList()));
        }

        return dto;
    }

    public static class FlyerPageResponseDto {
        private Integer id;
        private Integer pageNumber;
        private String imageUrl;
        private String thumbUrl;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public Integer getPageNumber() {
            return pageNumber;
        }

        public void setPageNumber(Integer pageNumber) {
            this.pageNumber = pageNumber;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        public String getThumbUrl() {
            return thumbUrl;
        }

        public void setThumbUrl(String thumbUrl) {
            this.thumbUrl = thumbUrl;
        }

        public static FlyerPageResponseDto fromEntity(FlyerPage page) {
            if (page == null) {
                return null;
            }
            FlyerPageResponseDto dto = new FlyerPageResponseDto();
            dto.setId(page.getId());
            dto.setPageNumber(page.getPageNumber());
            dto.setImageUrl(page.getImageUrl());
            dto.setThumbUrl(page.getThumbUrl());
            return dto;
        }
    }
}
