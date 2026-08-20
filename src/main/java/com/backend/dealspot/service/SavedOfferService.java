package com.backend.dealspot.service;

import java.util.List;

import com.backend.dealspot.dto.offer.OfferResponseDto;
import com.backend.dealspot.security.CustomUserPrincipal;

public interface SavedOfferService {

    boolean toggleSaveOffer(Long offerId, CustomUserPrincipal authUser);

    List<OfferResponseDto> getSavedOffers(CustomUserPrincipal authUser);

    boolean isOfferSaved(Long offerId, CustomUserPrincipal authUser);

    long getSavedCount(Long offerId);

}
