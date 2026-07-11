package com.backend.dealspot.service;

import java.util.List;

import com.backend.dealspot.dto.offer.OfferRequestDto;
import com.backend.dealspot.dto.offer.OfferResponseDto;
import com.backend.dealspot.security.CustomUserPrincipal;

import jakarta.servlet.http.HttpServletRequest;

public interface OfferService {

    OfferResponseDto addOffer(OfferRequestDto dto, CustomUserPrincipal authUser, HttpServletRequest request);

    List<OfferResponseDto> fetchAllOffers();

}
