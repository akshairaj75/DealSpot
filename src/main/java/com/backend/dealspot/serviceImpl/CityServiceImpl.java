package com.backend.dealspot.serviceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.backend.dealspot.dto.city.CityRegisterDto;
import com.backend.dealspot.dto.city.CityResponseDto;
import com.backend.dealspot.entity.AdminUser;
import com.backend.dealspot.entity.City;
import com.backend.dealspot.repository.AdminUserRepository;
import com.backend.dealspot.repository.CityRepository;
import com.backend.dealspot.security.CustomUserPrincipal;
import com.backend.dealspot.service.CityService;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class CityServiceImpl implements CityService {

    @Autowired
    AdminUserRepository adminUserRepository;

    @Autowired
    CityRepository cityRepository;

    @Override
    public CityResponseDto createCity(CityRegisterDto dto, CustomUserPrincipal principal, HttpServletRequest request) {

        AdminUser user = adminUserRepository.findById(principal.getId())
                .orElseThrow(() -> new UsernameNotFoundException("Admin not found"));

        City city = new City();
        city.setNameEn(dto.getNameEn());
        city.setNameAr(dto.getNameAr());
        city.setRegionCode(dto.getRegionCode());
        city.setLatitude(dto.getLatitude());
        city.setLongitude(dto.getLongitude());
        city.setActive(dto.isActive());
        City saved = cityRepository.save(city);
        return CityResponseDto.fromEntity(saved);
    }

    @Override
    public List<CityResponseDto> fetchAllCities(CustomUserPrincipal principal, HttpServletRequest request) {
        
        List<City> cities = cityRepository.findAll();
        return cities.stream().map(CityResponseDto::fromEntity).toList();

    }

    @Override
    public CityResponseDto updateCity(Integer cityId, CityRegisterDto dto, CustomUserPrincipal principal,
            HttpServletRequest request) {
        
        City city = cityRepository.findById(cityId)
                .orElseThrow(() -> new UsernameNotFoundException("City not found"));

        AdminUser user = adminUserRepository.findById(principal.getId())
                .orElseThrow(() -> new UsernameNotFoundException("Admin not found"));

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
        return CityResponseDto.fromEntity(saved);
    }

    @Override
    public void deleteCity(Integer cityId, CustomUserPrincipal principal, HttpServletRequest request) {
        
        City city = cityRepository.findById(cityId)
                .orElseThrow(() -> new UsernameNotFoundException("City not found"));

        AdminUser user = adminUserRepository.findById(principal.getId())
                .orElseThrow(() -> new UsernameNotFoundException("Admin not found"));

        cityRepository.delete(city);
    }

    @Override
    public CityResponseDto fetchCity(Integer cityId, CustomUserPrincipal principal, HttpServletRequest request) {
        City city = cityRepository.findById(cityId)
                .orElseThrow(() -> new UsernameNotFoundException("City not found"));
        return CityResponseDto.fromEntity(city);
    }   

}
