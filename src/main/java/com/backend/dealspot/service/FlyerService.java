package com.backend.dealspot.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.backend.dealspot.dto.flyer.FlyerRequestDto;
import com.backend.dealspot.dto.flyer.FlyerResponseDto;
import com.backend.dealspot.security.CustomUserPrincipal;

import jakarta.servlet.http.HttpServletRequest;

public interface FlyerService {

    FlyerResponseDto addFlyer(FlyerRequestDto flyerRequestDto, List<MultipartFile> pages, MultipartFile pdf, CustomUserPrincipal authUser,
            HttpServletRequest request);

    List<FlyerResponseDto> fetchAllFlyers();

    FlyerResponseDto fetchFlyerById(Integer flyerId);

}
