package com.backend.dealspot.service;

import java.util.List;

import com.backend.dealspot.dto.city.CityRegisterDto;
import com.backend.dealspot.dto.city.CityResponseDto;
import com.backend.dealspot.security.CustomUserPrincipal;

import jakarta.servlet.http.HttpServletRequest;

public interface CityService {

    CityResponseDto createCity(CityRegisterDto dto, CustomUserPrincipal principal, HttpServletRequest request);

    List<CityResponseDto> fetchAllCities(CustomUserPrincipal principal, HttpServletRequest request);

    CityResponseDto updateCity(Integer cityId, CityRegisterDto dto, CustomUserPrincipal principal,
            HttpServletRequest request);

    void deleteCity(Integer cityId, CustomUserPrincipal principal, HttpServletRequest request);

    CityResponseDto fetchCity(Integer cityId, CustomUserPrincipal principal, HttpServletRequest request);

    

}
