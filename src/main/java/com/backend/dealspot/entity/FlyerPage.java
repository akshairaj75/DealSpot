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
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
    name = "flyer_pages",
    uniqueConstraints = @UniqueConstraint(name = "uq_flyer_page", columnNames = {"flyer_id", "page_number"})
)
public class FlyerPage extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flyer_id", nullable = false)
    private Flyer flyer;

    @Column(name = "page_number", nullable = false)
    private Integer pageNumber;

    @Column(name = "image_url", nullable = false, length = 255)
    private String imageUrl;

    @Column(name = "thumb_url", length = 255)
    private String thumbUrl;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Flyer getFlyer() {
        return flyer;
    }

    public void setFlyer(Flyer flyer) {
        this.flyer = flyer;
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
}
