package com.backend.dealspot.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.backend.dealspot.entity.Offer;
import com.backend.dealspot.entity.SavedOffer;
import com.backend.dealspot.entity.User;

public interface SavedOfferRepository extends JpaRepository<SavedOffer, Long> {

    boolean existsByUserAndOffer(User user, Offer offer);

    Optional<SavedOffer> findByUserAndOffer(User user, Offer offer);

    List<SavedOffer> findByUserOrderBySavedAtDesc(User user);

    long countByOffer(Offer offer);

    long countByOfferId(Long offerId);

    @Query("SELECT so.offer.id FROM SavedOffer so WHERE so.user.id = :userId")
    Set<Long> findSavedOfferIdsByUserId(@Param("userId") Long userId);
}
