package com.backend.dealspot.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.backend.dealspot.entity.Flyer;

public interface FlyerRepository extends JpaRepository<Flyer, Integer> {
    List<Flyer> findByStoreId(Integer storeId);

    @Query("SELECT f FROM Flyer f WHERE f.active = true AND (f.validFrom IS NULL OR f.validFrom <= :today) AND (f.validUntil IS NULL OR f.validUntil >= :today)")
    List<Flyer> findActiveAndValidFlyers(@Param("today") LocalDate today);

    @Query("SELECT f FROM Flyer f WHERE f.active = true AND (f.validFrom IS NULL OR f.validFrom <= :today) AND (f.validUntil IS NULL OR f.validUntil >= :today) AND f.store.id = :storeId")
    List<Flyer> findActiveAndValidFlyersByStoreId(@Param("storeId") Integer storeId, @Param("today") LocalDate today);

    @Query("SELECT f FROM Flyer f " +
           "LEFT JOIN f.store s " +
           "LEFT JOIN f.city c " +
           "WHERE (:search IS NULL OR :search = '' OR " +
           " LOWER(f.titleEn) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           " LOWER(f.titleAr) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           " LOWER(s.nameEn) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           " LOWER(s.nameAr) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           " LOWER(c.nameEn) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           " LOWER(c.nameAr) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
           "(:storeId IS NULL OR s.id = :storeId) AND " +
           "(:cityId IS NULL OR c.id = :cityId) AND " +
           "(:status IS NULL OR :status = '' OR :status = 'ALL' OR " +
           " (:status = 'ACTIVE' AND f.active = true AND (f.validUntil IS NULL OR f.validUntil >= :today) AND (f.validFrom IS NULL OR f.validFrom <= :today)) OR " +
           " (:status = 'UNEXPIRED' AND f.active = true AND (f.validUntil IS NULL OR f.validUntil >= :today)) OR " +
           " (:status = 'EXPIRED' AND f.validUntil IS NOT NULL AND f.validUntil < :today) OR " +
           " (:status = 'UPCOMING' AND f.validFrom IS NOT NULL AND f.validFrom > :today) OR " +
           " (:status = 'DISABLED' AND f.active = false) OR " +
           " (:status = 'INACTIVE' AND f.active = false)) " +
           "ORDER BY f.id DESC")
    List<Flyer> searchFlyers(
            @Param("search") String search,
            @Param("storeId") Integer storeId,
            @Param("cityId") Integer cityId,
            @Param("status") String status,
            @Param("today") LocalDate today);
}

