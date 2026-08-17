package com.backend.dealspot.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.backend.dealspot.dto.brand.BrandDto;
import com.backend.dealspot.dto.brand.BrandRegisterDto;
import com.backend.dealspot.dto.brand.BrandResponseDto;
import com.backend.dealspot.service.BrandService;

@RestController
public class BrandController {

    @Autowired
    BrandService brandService;

    @PostMapping("/api/dealspot/brands/register-brand")
    public ResponseEntity<BrandResponseDto> registerBrand(
            @RequestPart("data") BrandRegisterDto dto,
            @RequestPart(value = "logoFile", required = false) MultipartFile logoFile,
            @RequestPart(value = "bannerFile", required = false) MultipartFile bannerFile) {

        BrandResponseDto res = brandService.registerBrand(dto, logoFile, bannerFile);
        return ResponseEntity.ok(res);
    }

    @PostMapping("/api/dealspot/brands/register-brand/bulk")
    public ResponseEntity<List<BrandResponseDto>> registerBulkBrands(
            @RequestPart("data") List<BrandRegisterDto> dto) {

                List<BrandResponseDto> res = dto.stream()
                .map(brandRegisterDto -> brandService.registerBrand(brandRegisterDto, null, null))
                .toList();
                return ResponseEntity.ok(res);
    }

    @GetMapping("/api/dealspot/brands/fetch-brands")
    public ResponseEntity<List<BrandResponseDto>> fetchBrands() {
        return ResponseEntity.ok(brandService.fetchBrands());
    }

    @GetMapping("/api/dealspot/brands/{id}")
    public ResponseEntity<BrandResponseDto> getBrand(@PathVariable Long id) {
        BrandResponseDto res = brandService.getBrandById(id);
        return ResponseEntity.ok(res);
    }

    @PatchMapping("/api/dealspot/brands/update-brand/{id}")
    public ResponseEntity<BrandResponseDto> updateBrand(
        @PathVariable Long id, 
        @RequestPart("data") BrandRegisterDto dto,
        @RequestPart(value = "logoFile", required = false) MultipartFile logoFile,
        @RequestPart(value = "bannerFile", required = false) MultipartFile bannerFile) {
        
        BrandResponseDto res = brandService.updateBrand(id, dto,logoFile,bannerFile);
        return ResponseEntity.ok(res);
    }

    @DeleteMapping("/api/dealspot/brands/delete-brand/{id}")
    public ResponseEntity<String> deleteBrand(@PathVariable Long id) {
        
        brandService.deleteBrand(id);
        return ResponseEntity.ok("Brand deleted successfully");
    }

    @GetMapping("/api/admin/brands/search")
    public ResponseEntity<Page<BrandDto>> searchBrands(
            @RequestParam(value = "q", required = false) String q,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", required = false) Integer size) {

        int pageSize = (size != null) ? size : 20;
        Page<BrandDto> res = brandService.searchBrands(q, page, pageSize);
        return ResponseEntity.ok(res);
    }

}
