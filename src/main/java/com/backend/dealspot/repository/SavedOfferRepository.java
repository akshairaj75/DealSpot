package com.backend.dealspot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.backend.dealspot.entity.SavedOffer;

public interface SavedOfferRepository extends JpaRepository<SavedOffer, Long> {
}
