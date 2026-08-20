package com.backend.dealspot.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.backend.dealspot.dto.offer.OfferRequestDto;
import com.backend.dealspot.dto.offer.OfferResponseDto;
import com.backend.dealspot.security.CustomUserPrincipal;

import jakarta.servlet.http.HttpServletRequest;

public interface OfferService {

    List<OfferResponseDto> fetchAllOffers(CustomUserPrincipal authUser, Integer storeId);

    OfferResponseDto getOfferById(Long offerId);

    OfferResponseDto updateOffer(Long offerId, OfferRequestDto dto, List<MultipartFile> images, CustomUserPrincipal authUser,
            HttpServletRequest request);

    OfferResponseDto addOffer(OfferRequestDto dto, List<MultipartFile> images, CustomUserPrincipal authUser,
            HttpServletRequest request);

    void deleteOffer(Long offerId, CustomUserPrincipal authUser);

}

