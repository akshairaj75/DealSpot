package com.backend.dealspot.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.backend.dealspot.dto.brand.BrandDto;
import com.backend.dealspot.dto.brand.BrandRegisterDto;

public interface BrandService {
    
    BrandDto registerBrand(BrandRegisterDto dto, MultipartFile logoFile, MultipartFile bannerFile);

    
    List<BrandDto> fetchBrands();


    BrandDto getBrandById(Long id);


	BrandDto updateBrand(Long id, BrandRegisterDto dto, MultipartFile logoFile, MultipartFile bannerFile);


    void deleteBrand(Long id);
}