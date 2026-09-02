package com.backend.dealspot.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.backend.dealspot.dto.offer.OfferRequestDto;
import com.backend.dealspot.dto.offer.OfferResponseDto;
import com.backend.dealspot.security.CustomUserPrincipal;

import jakarta.servlet.http.HttpServletRequest;

public interface OfferService {

    List<OfferResponseDto> fetchAllOffers(CustomUserPrincipal authUser, Integer storeId, Boolean includeExpired);

    OfferResponseDto getOfferById(Long offerId);

    OfferResponseDto updateOffer(Long offerId, OfferRequestDto dto, List<MultipartFile> images, CustomUserPrincipal authUser,
            HttpServletRequest request);

    OfferResponseDto addOffer(OfferRequestDto dto, List<MultipartFile> images, CustomUserPrincipal authUser,
            HttpServletRequest request);

    void deleteOffer(Long offerId, CustomUserPrincipal authUser);

    OfferResponseDto extendOffer(Long offerId, int days, CustomUserPrincipal authUser);

    org.springframework.data.domain.Page<OfferResponseDto> getPagedOffers(
            String search,
            Integer storeId,
            String badgeType,
            Boolean active,
            int page,
            int size);
}

