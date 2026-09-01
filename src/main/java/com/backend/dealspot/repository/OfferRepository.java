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
}

