package com.backend.dealspot.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.backend.dealspot.dto.flyer.FlyerRequestDto;
import com.backend.dealspot.dto.flyer.FlyerResponseDto;
import com.backend.dealspot.dto.flyer.FlyerResponseDto.FlyerPageResponseDto;
import com.backend.dealspot.security.CustomUserPrincipal;

import jakarta.servlet.http.HttpServletRequest;

public interface FlyerService {

    FlyerResponseDto addFlyer(FlyerRequestDto flyerRequestDto, List<MultipartFile> pages, MultipartFile pdf, CustomUserPrincipal authUser,
            HttpServletRequest request);

    FlyerResponseDto updateFlyer(Integer flyerId, FlyerRequestDto flyerRequestDto, List<MultipartFile> pages, MultipartFile pdf, CustomUserPrincipal authUser,
            HttpServletRequest request);

    List<FlyerResponseDto> fetchAllFlyers(CustomUserPrincipal authUser, Integer storeId);

    FlyerResponseDto fetchFlyerById(Integer flyerId);

    void deleteFlyer(Integer flyerId, CustomUserPrincipal authUser);

    List<FlyerPageResponseDto> fetchPagesByFlyerId(Integer flyerId);

    FlyerPageResponseDto addFlyerPage(Integer flyerId, Integer pageNumber, MultipartFile file, CustomUserPrincipal authUser);

    FlyerPageResponseDto updateFlyerPage(Integer pageId, Integer pageNumber, MultipartFile file, CustomUserPrincipal authUser);

    void deleteFlyerPage(Integer pageId, CustomUserPrincipal authUser);

}

