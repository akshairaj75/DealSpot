package com.backend.dealspot.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import com.backend.dealspot.dto.brand.BrandDto;
import com.backend.dealspot.dto.brand.BrandRegisterDto;
import com.backend.dealspot.dto.brand.BrandResponseDto;

public interface BrandService {
    
    BrandResponseDto registerBrand(BrandRegisterDto dto, MultipartFile logoFile, MultipartFile bannerFile);

    List<BrandResponseDto> fetchBrands();

    BrandResponseDto getBrandById(Long id);

    BrandResponseDto updateBrand(Long id, BrandRegisterDto dto, MultipartFile logoFile, MultipartFile bannerFile);

    void deleteBrand(Long id);

    Page<BrandDto> searchBrands(String q, int page, int size);
}