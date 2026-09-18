package com.backend.dealspot.service;

import com.backend.dealspot.dto.specialoffer.SpecialOfferRequestDto;
import com.backend.dealspot.dto.specialoffer.SpecialOfferResponseDto;
import com.backend.dealspot.security.CustomUserPrincipal;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface SpecialOfferService {

    SpecialOfferResponseDto addSpecialOffer(
            SpecialOfferRequestDto dto,
            MultipartFile bannerFile,
            CustomUserPrincipal authUser,
            HttpServletRequest request);

    SpecialOfferResponseDto updateSpecialOffer(
            Long id,
            SpecialOfferRequestDto dto,
            MultipartFile bannerFile,
            CustomUserPrincipal authUser,
            HttpServletRequest request);

    void deleteSpecialOffer(
            Long id,
            CustomUserPrincipal authUser,
            HttpServletRequest request);

    SpecialOfferResponseDto getSpecialOfferById(Long id);

    List<SpecialOfferResponseDto> fetchAllActiveSpecialOffers(Integer storeId, Integer cityId);

    Page<SpecialOfferResponseDto> getPagedSpecialOffers(
            CustomUserPrincipal authUser,
            String search,
            Integer storeId,
            String status,
            Boolean active,
            int page,
            int size);

    SpecialOfferResponseDto attachDeals(
            Long specialOfferId,
            List<Long> offerIds,
            CustomUserPrincipal authUser,
            HttpServletRequest request);

    SpecialOfferResponseDto removeDeal(
            Long specialOfferId,
            Long offerId,
            CustomUserPrincipal authUser,
            HttpServletRequest request);

    com.backend.dealspot.dto.offer.OfferResponseDto splitAndCreateCampaignOffer(
            Long specialOfferId,
            com.backend.dealspot.dto.offer.OfferPeriodSplitRequestDto dto,
            CustomUserPrincipal authUser,
            HttpServletRequest request);
}
