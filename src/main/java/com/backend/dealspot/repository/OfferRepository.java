package com.backend.dealspot.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.backend.dealspot.entity.Offer;

public interface OfferRepository extends JpaRepository<Offer, Long> {

    List<Offer> findAllByActive(boolean b);

    List<Offer> findByStoreId(Integer storeId);

    List<Offer> findByStoreIdAndActive(Integer storeId, boolean active);

    List<Offer> findByActiveTrueAndValidUntilBefore(LocalDate date);

    @org.springframework.data.jpa.repository.Query("SELECT o FROM Offer o WHERE o.active = true AND o.validFrom <= :today AND o.validUntil >= :today")
    List<Offer> findActiveAndValidOffers(@org.springframework.data.repository.query.Param("today") LocalDate today);

    @org.springframework.data.jpa.repository.Query("SELECT o FROM Offer o WHERE o.active = true AND o.validFrom <= :today AND o.validUntil >= :today AND o.store.id = :storeId")
    List<Offer> findActiveAndValidOffersByStoreId(@org.springframework.data.repository.query.Param("storeId") Integer storeId, @org.springframework.data.repository.query.Param("today") LocalDate today);
}

