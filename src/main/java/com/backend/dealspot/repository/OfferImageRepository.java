package com.backend.dealspot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.backend.dealspot.entity.OfferImage;

public interface OfferImageRepository extends JpaRepository<OfferImage, Long> {
}
