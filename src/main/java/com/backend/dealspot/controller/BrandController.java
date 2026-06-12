package com.backend.dealspot.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.backend.dealspot.dto.brand.BrandDto;
import com.backend.dealspot.dto.brand.BrandRegisterDto;
import com.backend.dealspot.service.BrandService;

@RestController
@RequestMapping("/api/dealspot/brands")
public class BrandController {

    @Autowired 
    BrandService brandService;

    @PostMapping("/register-brand")
    public ResponseEntity<BrandDto> registerBrand(
            @RequestPart("data") BrandRegisterDto dto,
            @RequestPart(value = "logoFile", required = false) MultipartFile logoFile,
            @RequestPart(value = "bannerFile", required = false) MultipartFile bannerFile) {

        BrandDto res = brandService.registerBrand(dto, logoFile, bannerFile);
        return ResponseEntity.ok(res);
    }

    @GetMapping("/fetch-brands")
    public ResponseEntity<List<BrandDto>> fetchBrands(){
        return ResponseEntity.ok(brandService.fetchBrands());
    }

}
