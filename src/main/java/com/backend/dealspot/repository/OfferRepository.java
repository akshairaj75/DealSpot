package com.backend.dealspot.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.backend.dealspot.entity.Offer;
import com.backend.dealspot.enums.OfferBadgeType;

public interface OfferRepository extends JpaRepository<Offer, Long> {

    List<Offer> findAllByActive(boolean b);

    List<Offer> findByStoreId(Integer storeId);

    List<Offer> findByStoreIdAndActive(Integer storeId, boolean active);

    List<Offer> findByActiveTrueAndValidUntilBefore(LocalDate date);

    @Query("SELECT o FROM Offer o WHERE o.active = true AND o.validFrom <= :today AND o.validUntil >= :today")
    List<Offer> findActiveAndValidOffers(@Param("today") LocalDate today);

    @Query("SELECT o FROM Offer o WHERE o.active = true AND o.validFrom <= :today AND o.validUntil >= :today AND o.store.id = :storeId")
    List<Offer> findActiveAndValidOffersByStoreId(@Param("storeId") Integer storeId, @Param("today") LocalDate today);

    @Query("SELECT o FROM Offer o WHERE " +
           "(:search IS NULL OR :search = '' OR " +
           " LOWER(o.titleEn) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           " LOWER(o.titleAr) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           " LOWER(o.store.nameEn) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           " LOWER(o.store.nameAr) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           " LOWER(o.category.nameEn) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           " LOWER(o.category.nameAr) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           " CAST(o.id AS string) LIKE CONCAT('%', :search, '%')) AND " +
           "(:storeId IS NULL OR o.store.id = :storeId) AND " +
           "(:badgeType IS NULL OR o.badgeType = :badgeType) AND " +
           "(:active IS NULL OR o.active = :active)")
    Page<Offer> searchOffers(
            @Param("search") String search,
            @Param("storeId") Integer storeId,
            @Param("badgeType") OfferBadgeType badgeType,
            @Param("active") Boolean active,
            Pageable pageable);
}

