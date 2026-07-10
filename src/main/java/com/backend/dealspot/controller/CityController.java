package com.backend.dealspot.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.dealspot.dto.city.CityRegisterDto;
import com.backend.dealspot.dto.city.CityResponseDto;
import com.backend.dealspot.security.CustomUserPrincipal;
import com.backend.dealspot.service.CityService;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping("/api/dealspot/cities")
public class CityController {

    @Autowired
    private CityService cityService;

    @PostMapping("/create")
    public ResponseEntity<CityResponseDto> createCity(
            @RequestBody CityRegisterDto dto,
            @AuthenticationPrincipal CustomUserPrincipal principal,
            HttpServletRequest request) {
        CityResponseDto createdCity = cityService.createCity(dto, principal, request);
        return ResponseEntity.ok(createdCity);

    }

    @GetMapping("/fetch-all")
    public ResponseEntity<List<CityResponseDto>> fetchAllCities(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            HttpServletRequest request) {
        List<CityResponseDto> cities = cityService.fetchAllCities(principal, request);
        return ResponseEntity.ok(cities);

    }

    @PutMapping("/edit/{cityId}")
    public ResponseEntity<CityResponseDto> updateCity(
            @RequestBody CityRegisterDto dto,
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @PathVariable Integer cityId,
            HttpServletRequest request) {
        CityResponseDto updatedCity = cityService.updateCity(cityId, dto, principal, request);
        return ResponseEntity.ok(updatedCity);

    }

    @DeleteMapping("/delete/{cityId}")
    public ResponseEntity<String> deleteCity(
            @PathVariable("cityId") Integer cityId,
            @AuthenticationPrincipal CustomUserPrincipal principal,
            HttpServletRequest request) {
        cityService.deleteCity(cityId, principal, request);
        return ResponseEntity.ok("City deleted successfully");

    }

    @GetMapping("/fetch/{cityId}")
    public ResponseEntity<CityResponseDto> fetchCity(
            @PathVariable("cityId") Integer cityId,
            @AuthenticationPrincipal CustomUserPrincipal principal,
            HttpServletRequest request) {
        CityResponseDto city = cityService.fetchCity(cityId, principal, request);
        return ResponseEntity.ok(city);

    }

}
