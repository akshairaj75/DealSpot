package com.backend.dealspot.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<OfferResponseDto> addOffer(
            @RequestPart("data") OfferRequestDto dto,
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            @AuthenticationPrincipal CustomUserPrincipal authUser,
            HttpServletRequest request) {

        return ResponseEntity.ok(
                offerService.addOffer(dto, images, authUser, request));
    }

    @GetMapping("/fetch-all-offers")
    public ResponseEntity<List<OfferResponseDto>> fetchAllOffers() {
        List<OfferResponseDto> result = offerService.fetchAllOffers();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/fetch-offer/{offerId}")
    public ResponseEntity<OfferResponseDto> getOfferById(
            @PathVariable("offerId") Long offerId) {
        OfferResponseDto result = offerService.getOfferById(offerId);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/update/{offerId}")
    public ResponseEntity<OfferResponseDto> updateOffer(
            @PathVariable("offerId") Long offerId,
            @RequestBody OfferRequestDto dto,
            @AuthenticationPrincipal CustomUserPrincipal authUser,
            HttpServletRequest request) {
        OfferResponseDto result = offerService.updateOffer(offerId, dto, authUser, request);
        return ResponseEntity.ok(result);
    }

}
