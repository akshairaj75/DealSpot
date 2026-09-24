package com.backend.dealspot.repository;

import com.backend.dealspot.entity.SpecialOffer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface SpecialOfferRepository extends JpaRepository<SpecialOffer, Long> {

    List<SpecialOffer> findAllByActive(boolean active);

    @EntityGraph(attributePaths = {"store", "city"})
    List<SpecialOffer> findByStoreId(Integer storeId);

    @EntityGraph(attributePaths = {"store", "city"})
    List<SpecialOffer> findByStoreIdAndActive(Integer storeId, boolean active);

    @EntityGraph(attributePaths = {"store", "city"})
    @Query("SELECT so FROM SpecialOffer so WHERE so.active = true " +
           "AND (so.validFrom IS NULL OR so.validFrom <= :today) " +
           "AND (so.validUntil IS NULL OR so.validUntil >= :today) " +
           "ORDER BY so.featured DESC, so.createdAt DESC")
    List<SpecialOffer> findActiveAndValidSpecialOffers(@Param("today") LocalDate today);

    @EntityGraph(attributePaths = {"store", "city"})
    @Query("SELECT so FROM SpecialOffer so " +
           "LEFT JOIN so.store s " +
           "LEFT JOIN so.city c " +
           "WHERE so.active = true " +
           "AND (so.validFrom IS NULL OR so.validFrom <= :today) " +
           "AND (so.validUntil IS NULL OR so.validUntil >= :today) " +
           "AND (:storeId IS NULL OR s.id = :storeId) " +
           "AND (:cityId IS NULL OR c.id = :cityId OR c.id IS NULL) " +
           "ORDER BY so.featured DESC, so.createdAt DESC")
    List<SpecialOffer> findActiveSpecialOffers(
            @Param("today") LocalDate today,
            @Param("storeId") Integer storeId,
            @Param("cityId") Integer cityId);

    @EntityGraph(attributePaths = {"store", "city"})
    @Query("SELECT so FROM SpecialOffer so " +
           "LEFT JOIN so.store s " +
           "WHERE (:search IS NULL OR :search = '' OR " +
           " LOWER(so.titleEn) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           " LOWER(so.titleAr) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           " LOWER(s.nameEn) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           " LOWER(s.nameAr) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           " CONCAT(so.id, '') LIKE CONCAT('%', :search, '%')) AND " +
           "(:storeId IS NULL OR s.id = :storeId) AND " +
           "(:status IS NULL OR :status = '' OR " +
           " (:status = 'ACTIVE' AND so.active = true AND (so.validUntil IS NULL OR so.validUntil >= :today) AND (so.validFrom IS NULL OR so.validFrom <= :today)) OR " +
           " (:status = 'EXPIRED' AND so.validUntil IS NOT NULL AND so.validUntil < :today) OR " +
           " (:status = 'UPCOMING' AND so.validFrom IS NOT NULL AND so.validFrom > :today) OR " +
           " (:status = 'DISABLED' AND so.active = false)) AND " +
           "(:active IS NULL OR so.active = :active)")
    Page<SpecialOffer> searchSpecialOffers(
            @Param("search") String search,
            @Param("storeId") Integer storeId,
            @Param("status") String status,
            @Param("today") LocalDate today,
            @Param("active") Boolean active,
            Pageable pageable);

    @EntityGraph(attributePaths = {"store", "city"})
    Optional<SpecialOffer> findDetailedById(Long id);
}
