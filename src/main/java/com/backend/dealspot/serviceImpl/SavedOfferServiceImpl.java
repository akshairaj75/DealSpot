package com.backend.dealspot.serviceImpl;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backend.dealspot.dto.offer.OfferResponseDto;
import com.backend.dealspot.entity.Offer;
import com.backend.dealspot.entity.SavedOffer;
import com.backend.dealspot.entity.User;
import com.backend.dealspot.repository.OfferRepository;
import com.backend.dealspot.repository.SavedOfferRepository;
import com.backend.dealspot.repository.UserRepository;
import com.backend.dealspot.security.CustomUserPrincipal;
import com.backend.dealspot.service.SavedOfferService;

@Service
public class SavedOfferServiceImpl implements SavedOfferService {

    private final SavedOfferRepository savedOfferRepository;
    private final OfferRepository offerRepository;
    private final UserRepository userRepository;

    public SavedOfferServiceImpl(
            SavedOfferRepository savedOfferRepository,
            OfferRepository offerRepository,
            UserRepository userRepository) {
        this.savedOfferRepository = savedOfferRepository;
        this.offerRepository = offerRepository;
        this.userRepository = userRepository;
    }

    private User getAuthenticatedUser(CustomUserPrincipal authUser) {
        if (authUser == null || authUser.getId() == null) {
            throw new AccessDeniedException("User must be authenticated to perform this action");
        }
        return userRepository.findById(authUser.getId())
                .orElseThrow(() -> new IllegalArgumentException("User account not found"));
    }

    @Transactional
    @Override
    public boolean toggleSaveOffer(Long offerId, CustomUserPrincipal authUser) {
        User user = getAuthenticatedUser(authUser);
        Offer offer = offerRepository.findById(offerId)
                .orElseThrow(() -> new IllegalArgumentException("Offer not found with id: " + offerId));

        Optional<SavedOffer> existing = savedOfferRepository.findByUserAndOffer(user, offer);
        if (existing.isPresent()) {
            savedOfferRepository.delete(existing.get());
            // Decrement saveCount on Offer
            int currentCount = offer.getSaveCount() != null ? offer.getSaveCount() : 0;
            offer.setSaveCount(Math.max(0, currentCount - 1));
            offerRepository.save(offer);
            return false; // Unsaved
        } else {
            SavedOffer savedOffer = new SavedOffer();
            savedOffer.setUser(user);
            savedOffer.setOffer(offer);
            savedOfferRepository.save(savedOffer);
            // Increment saveCount on Offer
            int currentCount = offer.getSaveCount() != null ? offer.getSaveCount() : 0;
            offer.setSaveCount(currentCount + 1);
            offerRepository.save(offer);
            return true; // Saved
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<OfferResponseDto> getSavedOffers(CustomUserPrincipal authUser) {
        if (authUser == null || authUser.getId() == null) {
            return Collections.emptyList();
        }
        User user = userRepository.findById(authUser.getId()).orElse(null);
        if (user == null) {
            return Collections.emptyList();
        }

        List<SavedOffer> savedList = savedOfferRepository.findByUserOrderBySavedAtDesc(user);
        return savedList.stream()
                .map(s -> {
                    OfferResponseDto dto = OfferResponseDto.fromEntity(s.getOffer());
                    if (dto != null) {
                        dto.setSaved(true);
                    }
                    return dto;
                })
                .toList();
    }

    @Transactional(readOnly = true)
    @Override
    public boolean isOfferSaved(Long offerId, CustomUserPrincipal authUser) {
        if (authUser == null || authUser.getId() == null) {
            return false;
        }
        User user = userRepository.findById(authUser.getId()).orElse(null);
        if (user == null) {
            return false;
        }
        Offer offer = offerRepository.findById(offerId).orElse(null);
        if (offer == null) {
            return false;
        }
        return savedOfferRepository.existsByUserAndOffer(user, offer);
    }

    @Transactional(readOnly = true)
    @Override
    public long getSavedCount(Long offerId) {
        return savedOfferRepository.countByOfferId(offerId);
    }

}
