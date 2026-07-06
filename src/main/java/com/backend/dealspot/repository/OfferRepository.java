package com.backend.dealspot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.backend.dealspot.entity.Offer;

public interface OfferRepository extends JpaRepository<Offer, Long> {
}
