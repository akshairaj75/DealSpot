package com.backend.dealspot.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.dealspot.dto.offer.OfferRequestDto;
import com.backend.dealspot.dto.offer.OfferResponseDto;
import com.backend.dealspot.security.CustomUserPrincipal;
import com.backend.dealspot.service.OfferService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/dealspot/offers")
public class OfferController {

    @Autowired
    OfferService offerService;

    @PostMapping("/create")
    public ResponseEntity<OfferResponseDto> addOffer(
            @RequestBody OfferRequestDto dto,
            @AuthenticationPrincipal CustomUserPrincipal authUser,
            HttpServletRequest request) {
        return ResponseEntity.ok(offerService.addOffer(dto, authUser, request));
    }

    @GetMapping("/fetch-all-offers")
    public ResponseEntity<List<OfferResponseDto>> fetchAllOffers() {
        List<OfferResponseDto> result = offerService.fetchAllOffers();
        return ResponseEntity.ok(result);
    }

    @PostMapping("/create/bulk")
    public ResponseEntity<List<OfferResponseDto>> addBulkOffers(
            @RequestBody List<OfferRequestDto> dto,
            @AuthenticationPrincipal CustomUserPrincipal authUser,
            HttpServletRequest request) {

        List<OfferResponseDto> res = dto.stream()
                .map(offerRequestDto -> offerService.addOffer(offerRequestDto, authUser, request))
                .toList();
        return ResponseEntity.ok(res);
    }

    // @GetMapping("/fetch-offer/{offerId}")
    // public ResponseEntity<OfferResponseDto> getOfferById(
    //         @PathVariable("offerId") Long offerId) {
    //     OfferResponseDto result = offerService.getOfferById(offerId);
    //     return ResponseEntity.ok(result);
    // }



}
