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

    @Query("SELECT o FROM Offer o WHERE o.active = true AND (o.validFrom IS NULL OR o.validFrom <= :today) AND (o.validUntil IS NULL OR o.validUntil >= :today)")
    List<Offer> findActiveAndValidOffers(@Param("today") LocalDate today);

    @Query("SELECT o FROM Offer o WHERE o.active = true AND (o.validFrom IS NULL OR o.validFrom <= :today) AND (o.validUntil IS NULL OR o.validUntil >= :today) AND o.store.id = :storeId")
    List<Offer> findActiveAndValidOffersByStoreId(@Param("storeId") Integer storeId, @Param("today") LocalDate today);

    @Query("SELECT o FROM Offer o " +
           "LEFT JOIN o.store s " +
           "LEFT JOIN o.category c " +
           "WHERE (:search IS NULL OR :search = '' OR " +
           " LOWER(o.titleEn) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           " LOWER(o.titleAr) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           " LOWER(s.nameEn) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           " LOWER(s.nameAr) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           " LOWER(c.nameEn) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           " LOWER(c.nameAr) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           " CONCAT(o.id, '') LIKE CONCAT('%', :search, '%')) AND " +
           "(:storeId IS NULL OR s.id = :storeId) AND " +
           "(:badgeType IS NULL OR (:badgeType = com.backend.dealspot.enums.OfferBadgeType.NONE AND (o.badgeType IS NULL OR o.badgeType = :badgeType)) OR o.badgeType = :badgeType) AND " +
           "(:status IS NULL OR :status = '' OR " +
           " (:status = 'ACTIVE' AND o.active = true AND (o.validUntil IS NULL OR o.validUntil >= :today) AND (o.validFrom IS NULL OR o.validFrom <= :today)) OR " +
           " (:status = 'EXPIRED' AND o.validUntil IS NOT NULL AND o.validUntil < :today) OR " +
           " (:status = 'UPCOMING' AND o.validFrom IS NOT NULL AND o.validFrom > :today) OR " +
           " (:status = 'DISABLED' AND o.active = false)) AND " +
           "(:active IS NULL OR o.active = :active)")
    Page<Offer> searchOffers(
            @Param("search") String search,
            @Param("storeId") Integer storeId,
            @Param("badgeType") OfferBadgeType badgeType,
            @Param("status") String status,
            @Param("today") LocalDate today,
            @Param("active") Boolean active,
            Pageable pageable);
}

