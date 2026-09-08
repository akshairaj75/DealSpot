package com.backend.dealspot.serviceImpl;

import java.util.List;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backend.dealspot.dto.city.CityRegisterDto;
import com.backend.dealspot.dto.city.CityResponseDto;
import com.backend.dealspot.entity.City;
import com.backend.dealspot.enums.AuditAction;
import com.backend.dealspot.repository.CityRepository;
import com.backend.dealspot.security.CustomUserPrincipal;
import com.backend.dealspot.service.AuditLogService;
import com.backend.dealspot.service.CityService;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class CityServiceImpl implements CityService {

    private final CityRepository cityRepository;
    private final AuditLogService auditLogService;

    public CityServiceImpl(CityRepository cityRepository, AuditLogService auditLogService) {
        this.cityRepository = cityRepository;
        this.auditLogService = auditLogService;
    }

    @Transactional
    @Override
    public CityResponseDto createCity(CityRegisterDto dto, CustomUserPrincipal principal, HttpServletRequest request) {

        City city = new City();
        city.setNameEn(dto.getNameEn());
        city.setNameAr(dto.getNameAr());
        city.setRegionCode(dto.getRegionCode());
        city.setLatitude(dto.getLatitude());
        city.setLongitude(dto.getLongitude());
        city.setActive(dto.isActive());
        City saved = cityRepository.save(city);

        java.util.Map<String, Object> createAuditPayload = new java.util.HashMap<>();
        createAuditPayload.put("nameEn", saved.getNameEn());
        createAuditPayload.put("nameAr", saved.getNameAr());
        createAuditPayload.put("regionCode", saved.getRegionCode());
        auditLogService.logAction("CITY", saved.getId().longValue(), principal, AuditAction.CREATE, createAuditPayload, request);

        return CityResponseDto.fromEntity(saved);
    }

    @Override
    public List<CityResponseDto> fetchAllCities(CustomUserPrincipal principal, HttpServletRequest request) {
        List<City> cities = cityRepository.findAll();
        return cities.stream().map(CityResponseDto::fromEntity).toList();
    }

    @Transactional
    @Override
    public CityResponseDto updateCity(Integer cityId, CityRegisterDto dto, CustomUserPrincipal principal,
            HttpServletRequest request) {
        
        City city = cityRepository.findById(cityId)
                .orElseThrow(() -> new UsernameNotFoundException("City not found"));

        if (dto.getNameEn() != null && !dto.getNameEn().isEmpty()) {
            city.setNameEn(dto.getNameEn());
        }

        if (dto.getNameAr() != null && !dto.getNameAr().isEmpty()) {
            city.setNameAr(dto.getNameAr());
        }

        if (dto.getRegionCode() != null && !dto.getRegionCode().isEmpty()) {
            city.setRegionCode(dto.getRegionCode());
        }

        if (dto.getLatitude() != null) {
            city.setLatitude(dto.getLatitude());
        }

        if (dto.getLongitude() != null) {
            city.setLongitude(dto.getLongitude());
        }

        if (dto.isActive() == true || dto.isActive() == false) {
            city.setActive(dto.isActive());
        }

        City saved = cityRepository.save(city);

        java.util.Map<String, Object> updateAuditPayload = new java.util.HashMap<>();
        updateAuditPayload.put("nameEn", saved.getNameEn());
        updateAuditPayload.put("nameAr", saved.getNameAr());
        auditLogService.logAction("CITY", saved.getId().longValue(), principal, AuditAction.UPDATE, updateAuditPayload, request);

        return CityResponseDto.fromEntity(saved);
    }

    @Override
    public void deleteCity(Integer cityId, CustomUserPrincipal principal, HttpServletRequest request) {
        City city = cityRepository.findById(cityId)
                .orElseThrow(() -> new UsernameNotFoundException("City not found"));

        java.util.Map<String, Object> deleteAuditPayload = new java.util.HashMap<>();
        deleteAuditPayload.put("nameEn", city.getNameEn());
        deleteAuditPayload.put("nameAr", city.getNameAr());

        cityRepository.delete(city);

        auditLogService.logAction("CITY", cityId.longValue(), principal, AuditAction.DELETE, deleteAuditPayload, request);
    }

    @Override
    public CityResponseDto fetchCity(Integer cityId, CustomUserPrincipal principal, HttpServletRequest request) {
        City city = cityRepository.findById(cityId)
                .orElseThrow(() -> new UsernameNotFoundException("City not found"));
        return CityResponseDto.fromEntity(city);
    }   

}
