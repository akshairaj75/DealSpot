package com.backend.dealspot.service;

import java.util.List;

import com.backend.dealspot.dto.store.StoreRegisterDto;
import com.backend.dealspot.dto.store.StoreResponseDto;
import com.backend.dealspot.security.CustomUserPrincipal;

import jakarta.servlet.http.HttpServletRequest;

public interface StoreService {

    StoreResponseDto createStore(StoreRegisterDto dto, CustomUserPrincipal authUser, HttpServletRequest request);

    List<StoreResponseDto> fetchAllStores(CustomUserPrincipal authUser, HttpServletRequest request);

    StoreResponseDto fetchStore(Integer storeId, CustomUserPrincipal authUser, HttpServletRequest request);

    StoreResponseDto updateStore(Integer storeId, StoreRegisterDto dto, CustomUserPrincipal authUser,
            HttpServletRequest request);

    void deleteStore(Integer storeId, CustomUserPrincipal authUser, HttpServletRequest request);

}
