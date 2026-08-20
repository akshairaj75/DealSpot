package com.backend.dealspot.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.dealspot.dto.offer.OfferResponseDto;
import com.backend.dealspot.security.CustomUserPrincipal;
import com.backend.dealspot.service.SavedOfferService;

@RestController
@RequestMapping("/api/dealspot/offers")
public class SavedOfferController {

    private final SavedOfferService savedOfferService;

    public SavedOfferController(SavedOfferService savedOfferService) {
        this.savedOfferService = savedOfferService;
    }

    @PostMapping("/{offerId}/save-toggle")
    public ResponseEntity<Map<String, Object>> toggleSaveOffer(
            @PathVariable Long offerId,
            @AuthenticationPrincipal CustomUserPrincipal authUser) {
        boolean isSaved = savedOfferService.toggleSaveOffer(offerId, authUser);
        long count = savedOfferService.getSavedCount(offerId);
        return ResponseEntity.ok(Map.of(
                "isSaved", isSaved,
                "saveCount", count,
                "message", isSaved ? "Offer saved to favorites" : "Offer removed from favorites"
        ));
    }

    @GetMapping("/my-saved")
    public ResponseEntity<List<OfferResponseDto>> getMySavedOffers(
            @AuthenticationPrincipal CustomUserPrincipal authUser) {
        List<OfferResponseDto> savedOffers = savedOfferService.getSavedOffers(authUser);
        return ResponseEntity.ok(savedOffers);
    }

    @GetMapping("/{offerId}/is-saved")
    public ResponseEntity<Map<String, Boolean>> isOfferSaved(
            @PathVariable Long offerId,
            @AuthenticationPrincipal CustomUserPrincipal authUser) {
        boolean isSaved = savedOfferService.isOfferSaved(offerId, authUser);
        return ResponseEntity.ok(Map.of("isSaved", isSaved));
    }

}
