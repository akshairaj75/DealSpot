package com.backend.dealspot.controller;

import java.util.List;

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

    private final OfferService offerService;

    public OfferController(OfferService offerService) {
        this.offerService = offerService;
    }

    @PostMapping(value = "/create", consumes = { "multipart/form-data" })
    public ResponseEntity<OfferResponseDto> addOffer(
            @RequestPart("data") OfferRequestDto dto,
            @RequestPart(value = "files", required = false) List<MultipartFile> files,
            @AuthenticationPrincipal CustomUserPrincipal authUser,
            HttpServletRequest request) {

        return ResponseEntity.ok(
                offerService.addOffer(dto, files, authUser, request));
    }

    @PostMapping(value = "/create", consumes = { "application/json" })
    public ResponseEntity<OfferResponseDto> addOfferJson(
            @RequestBody OfferRequestDto dto,
            @AuthenticationPrincipal CustomUserPrincipal authUser,
            HttpServletRequest request) {

        return ResponseEntity.ok(
                offerService.addOffer(dto, null, authUser, request));
    }

    @GetMapping("/fetch-all-offers")
    public ResponseEntity<List<OfferResponseDto>> fetchAllOffers(
            @AuthenticationPrincipal CustomUserPrincipal authUser,
            @org.springframework.web.bind.annotation.RequestParam(value = "storeId", required = false) Integer storeId) {
        List<OfferResponseDto> result = offerService.fetchAllOffers(authUser, storeId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/fetch-offer/{offerId}")
    public ResponseEntity<OfferResponseDto> getOfferById(
            @PathVariable("offerId") Long offerId) {
        OfferResponseDto result = offerService.getOfferById(offerId);
        return ResponseEntity.ok(result);
    }

    @PutMapping(value = "/update/{offerId}", consumes = { "multipart/form-data" })
    public ResponseEntity<OfferResponseDto> updateOfferMultipart(
            @PathVariable("offerId") Long offerId,
            @RequestPart("data") OfferRequestDto dto,
            @RequestPart(value = "files", required = false) List<MultipartFile> files,
            @AuthenticationPrincipal CustomUserPrincipal authUser,
            HttpServletRequest request) {
        OfferResponseDto result = offerService.updateOffer(offerId, dto, files, authUser, request);
        return ResponseEntity.ok(result);
    }

    @PutMapping(value = "/update/{offerId}", consumes = { "application/json" })
    public ResponseEntity<OfferResponseDto> updateOfferJson(
            @PathVariable("offerId") Long offerId,
            @RequestBody OfferRequestDto dto,
            @AuthenticationPrincipal CustomUserPrincipal authUser,
            HttpServletRequest request) {
        OfferResponseDto result = offerService.updateOffer(offerId, dto, null, authUser, request);
        return ResponseEntity.ok(result);
    }

    @org.springframework.web.bind.annotation.DeleteMapping("/delete/{offerId}")
    public ResponseEntity<String> deleteOffer(
            @PathVariable("offerId") Long offerId,
            @AuthenticationPrincipal CustomUserPrincipal authUser) {
        offerService.deleteOffer(offerId, authUser);
        return ResponseEntity.ok("Offer deleted successfully");
    }


}
