package com.backend.dealspot.entity;

import jakarta.persistence.Index;
import org.hibernate.annotations.Check;

import com.backend.dealspot.enums.OfferBadgeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
    name = "offers",
    indexes = {
        @Index(name = "idx_offers_store", columnList = "store_id"),
        @Index(name = "idx_offers_product", columnList = "product_id"),
        @Index(name = "idx_offers_category", columnList = "category_id"),
        @Index(name = "idx_offers_city", columnList = "city_id"),
        @Index(name = "idx_offers_validity", columnList = "valid_from, valid_until"),
        @Index(name = "idx_offers_featured", columnList = "is_featured"),
        @Index(name = "idx_offers_flash", columnList = "is_flash"),
        @Index(name = "idx_offers_active", columnList = "is_active"),
        @Index(name = "idx_offers_badge", columnList = "badge_type"),
        @Index(name = "idx_offers_discount", columnList = "discount_pct")
    }
)
@Check(name = "chk_offers_price", constraints = "offer_price <= original_price")
@Check(name = "chk_offers_price_positive", constraints = "offer_price > 0")
@Check(name = "chk_offers_discount", constraints = "discount_pct BETWEEN 0 AND 100")
@Check(name = "chk_offers_dates", constraints = "valid_until >= valid_from")
public class Offer extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "city_id", nullable = false)
    private City city;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(name = "title_en", nullable = false, length = 200)
    private String titleEn;

    @Column(name = "title_ar", nullable = false, length = 200)
    private String titleAr;

    @Column(name = "description_en", columnDefinition = "TEXT")
    private String descriptionEn;

    @Column(name = "description_ar", columnDefinition = "TEXT")
    private String descriptionAr;

    @Column(name = "terms_en", columnDefinition = "TEXT")
    private String termsEn;

    @Column(name = "terms_ar", columnDefinition = "TEXT")
    private String termsAr;

    @Column(name = "original_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal originalPrice;

    @Column(name = "offer_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal offerPrice;

    @Column(name = "discount_pct", nullable = false)
    private Integer discountPct = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "badge_type", nullable = false, length = 20)
    private OfferBadgeType badgeType;

    @Column(name = "image_url", length = 255)
    private String imageUrl;

    @Column(name = "thumbnail_url", length = 255)
    private String thumbnailUrl;

    @Column(name = "valid_from", nullable = false)
    private LocalDate validFrom;

    @Column(name = "valid_until", nullable = false)
    private LocalDate validUntil;

    @Column(name = "is_featured", nullable = false)
    private boolean featured = false;

    @Column(name = "is_flash", nullable = false)
    private boolean flash = false;

    @Column(name = "is_online", nullable = false)
    private boolean online = false;

    @Column(name = "is_in_store", nullable = false)
    private boolean inStore = true;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    @Column(name = "view_count", nullable = false)
    private Long viewCount = 0L;

    @Column(name = "save_count", nullable = false)
    private Integer saveCount = 0;

    @Column(name = "share_count", nullable = false)
    private Integer shareCount = 0;

    @OneToMany(mappedBy = "offer")
    private List<OfferImage> images = new ArrayList<>();

    @OneToMany(mappedBy = "offer")
    private List<SavedOffer> savedOffers = new ArrayList<>();

    @OneToMany(mappedBy = "offer")
    private List<CouponCode> couponCodes = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Store getStore() {
        return store;
    }

    public void setStore(Store store) {
        this.store = store;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
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

    public String getTermsEn() {
        return termsEn;
    }

    public void setTermsEn(String termsEn) {
        this.termsEn = termsEn;
    }

    public String getTermsAr() {
        return termsAr;
    }

    public void setTermsAr(String termsAr) {
        this.termsAr = termsAr;
    }

    public BigDecimal getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(BigDecimal originalPrice) {
        this.originalPrice = originalPrice;
    }

    public BigDecimal getOfferPrice() {
        return offerPrice;
    }

    public void setOfferPrice(BigDecimal offerPrice) {
        this.offerPrice = offerPrice;
    }

    public Integer getDiscountPct() {
        return discountPct;
    }

    public void setDiscountPct(Integer discountPct) {
        this.discountPct = discountPct;
    }

    public OfferBadgeType getBadgeType() {
        return badgeType;
    }

    public void setBadgeType(OfferBadgeType badgeType) {
        this.badgeType = badgeType;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
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

    public boolean isFeatured() {
        return featured;
    }

    public void setFeatured(boolean featured) {
        this.featured = featured;
    }

    public boolean isFlash() {
        return flash;
    }

    public void setFlash(boolean flash) {
        this.flash = flash;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public boolean isInStore() {
        return inStore;
    }

    public void setInStore(boolean inStore) {
        this.inStore = inStore;
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

    public Integer getSaveCount() {
        return saveCount;
    }

    public void setSaveCount(Integer saveCount) {
        this.saveCount = saveCount;
    }

    public Integer getShareCount() {
        return shareCount;
    }

    public void setShareCount(Integer shareCount) {
        this.shareCount = shareCount;
    }

    public List<OfferImage> getImages() {
        return images;
    }

    public void setImages(List<OfferImage> images) {
        this.images = images;
    }

    public List<SavedOffer> getSavedOffers() {
        return savedOffers;
    }

    public void setSavedOffers(List<SavedOffer> savedOffers) {
        this.savedOffers = savedOffers;
    }

    public List<CouponCode> getCouponCodes() {
        return couponCodes;
    }

    public void setCouponCodes(List<CouponCode> couponCodes) {
        this.couponCodes = couponCodes;
    }
}
