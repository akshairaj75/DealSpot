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
}

