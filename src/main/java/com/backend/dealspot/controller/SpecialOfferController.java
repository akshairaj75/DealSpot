package com.backend.dealspot.controller;

import com.backend.dealspot.dto.offer.OfferPeriodSplitRequestDto;
import com.backend.dealspot.dto.offer.OfferResponseDto;
import com.backend.dealspot.dto.specialoffer.SpecialOfferRequestDto;
import com.backend.dealspot.dto.specialoffer.SpecialOfferResponseDto;
import com.backend.dealspot.security.CustomUserPrincipal;
import com.backend.dealspot.service.SpecialOfferService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/dealspot/special-offers")
public class SpecialOfferController {

    private final SpecialOfferService specialOfferService;

    public SpecialOfferController(SpecialOfferService specialOfferService) {
        this.specialOfferService = specialOfferService;
    }

    @GetMapping("/fetch-all")
    public ResponseEntity<List<SpecialOfferResponseDto>> fetchAllActive(
            @RequestParam(value = "storeId", required = false) Integer storeId,
            @RequestParam(value = "cityId", required = false) Integer cityId) {
        List<SpecialOfferResponseDto> list = specialOfferService.fetchAllActiveSpecialOffers(storeId, cityId);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/fetch/{id}")
    public ResponseEntity<SpecialOfferResponseDto> getSpecialOfferById(@PathVariable("id") Long id) {
        SpecialOfferResponseDto result = specialOfferService.getSpecialOfferById(id);
        return ResponseEntity.ok(result);
    }

    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('CONTENT_MANAGER') or hasRole('STORE_MANAGER')")
    @GetMapping("/paged")
    public ResponseEntity<Page<SpecialOfferResponseDto>> getPagedSpecialOffers(
            @AuthenticationPrincipal CustomUserPrincipal authUser,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "storeId", required = false) Integer storeId,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "active", required = false) Boolean active,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        Page<SpecialOfferResponseDto> pageResult = specialOfferService.getPagedSpecialOffers(
                authUser, search, storeId, status, active, page, size);
        return ResponseEntity.ok(pageResult);
    }

    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('CONTENT_MANAGER') or hasRole('STORE_MANAGER')")
    @PostMapping(value = "/create", consumes = { "multipart/form-data" })
    public ResponseEntity<SpecialOfferResponseDto> addSpecialOfferMultipart(
            @RequestPart("data") SpecialOfferRequestDto dto,
            @RequestPart(value = "banner", required = false) MultipartFile bannerFile,
            @AuthenticationPrincipal CustomUserPrincipal authUser,
            HttpServletRequest request) {
        SpecialOfferResponseDto result = specialOfferService.addSpecialOffer(dto, bannerFile, authUser, request);
        return ResponseEntity.ok(result);
    }

    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('CONTENT_MANAGER') or hasRole('STORE_MANAGER')")
    @PostMapping(value = "/create", consumes = { "application/json" })
    public ResponseEntity<SpecialOfferResponseDto> addSpecialOfferJson(
            @RequestBody SpecialOfferRequestDto dto,
            @AuthenticationPrincipal CustomUserPrincipal authUser,
            HttpServletRequest request) {
        SpecialOfferResponseDto result = specialOfferService.addSpecialOffer(dto, null, authUser, request);
        return ResponseEntity.ok(result);
    }

    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('CONTENT_MANAGER') or hasRole('STORE_MANAGER')")
    @PutMapping(value = "/update/{id}", consumes = { "multipart/form-data" })
    public ResponseEntity<SpecialOfferResponseDto> updateSpecialOfferMultipart(
            @PathVariable("id") Long id,
            @RequestPart("data") SpecialOfferRequestDto dto,
            @RequestPart(value = "banner", required = false) MultipartFile bannerFile,
            @AuthenticationPrincipal CustomUserPrincipal authUser,
            HttpServletRequest request) {
        SpecialOfferResponseDto result = specialOfferService.updateSpecialOffer(id, dto, bannerFile, authUser, request);
        return ResponseEntity.ok(result);
    }

    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('CONTENT_MANAGER') or hasRole('STORE_MANAGER')")
    @PutMapping(value = "/update/{id}", consumes = { "application/json" })
    public ResponseEntity<SpecialOfferResponseDto> updateSpecialOfferJson(
            @PathVariable("id") Long id,
            @RequestBody SpecialOfferRequestDto dto,
            @AuthenticationPrincipal CustomUserPrincipal authUser,
            HttpServletRequest request) {
        SpecialOfferResponseDto result = specialOfferService.updateSpecialOffer(id, dto, null, authUser, request);
        return ResponseEntity.ok(result);
    }

    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('CONTENT_MANAGER') or hasRole('STORE_MANAGER')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteSpecialOffer(
            @PathVariable("id") Long id,
            @AuthenticationPrincipal CustomUserPrincipal authUser,
            HttpServletRequest request) {
        specialOfferService.deleteSpecialOffer(id, authUser, request);
        return ResponseEntity.ok("Special offer deleted successfully");
    }

    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('CONTENT_MANAGER') or hasRole('STORE_MANAGER')")
    @PostMapping("/{id}/attach-deals")
    public ResponseEntity<SpecialOfferResponseDto> attachDeals(
            @PathVariable("id") Long id,
            @RequestBody List<Long> offerIds,
            @AuthenticationPrincipal CustomUserPrincipal authUser,
            HttpServletRequest request) {
        SpecialOfferResponseDto result = specialOfferService.attachDeals(id, offerIds, authUser, request);
        return ResponseEntity.ok(result);
    }

    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('CONTENT_MANAGER') or hasRole('STORE_MANAGER')")
    @DeleteMapping("/{id}/remove-deal/{offerId}")
    public ResponseEntity<SpecialOfferResponseDto> removeDeal(
            @PathVariable("id") Long id,
            @PathVariable("offerId") Long offerId,
            @AuthenticationPrincipal CustomUserPrincipal authUser,
            HttpServletRequest request) {
        SpecialOfferResponseDto result = specialOfferService.removeDeal(id, offerId, authUser, request);
        return ResponseEntity.ok(result);
    }

    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('CONTENT_MANAGER') or hasRole('STORE_MANAGER')")
    @PostMapping(value = "/{id}/split-offer-price", consumes = { "application/json" })
    public ResponseEntity<OfferResponseDto> splitOfferPrice(
            @PathVariable("id") Long id,
            @RequestBody OfferPeriodSplitRequestDto dto,
            @AuthenticationPrincipal CustomUserPrincipal authUser,
            HttpServletRequest request) {
        OfferResponseDto result = specialOfferService.splitAndCreateCampaignOffer(id, dto, authUser, request);
        return ResponseEntity.ok(result);
    }
}

