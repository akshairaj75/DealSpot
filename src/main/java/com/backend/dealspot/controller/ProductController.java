package com.backend.dealspot.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.backend.dealspot.dto.product.ProductRegisterDto;
import com.backend.dealspot.dto.product.ProductResponseDto;
import com.backend.dealspot.service.ProductService;

@RestController
@RequestMapping("/api/dealspot/products")
public class ProductController {

    @Autowired
    ProductService productService;

    @PostMapping("/add-product")
    public ResponseEntity<ProductResponseDto> registerProduct(
        @RequestPart("data") ProductRegisterDto dto,
        @RequestPart(value = "file", required = false) List<MultipartFile> file
    ){
        
        ProductResponseDto res =  productService.registerProduct(dto, file);
        return ResponseEntity.ok(res);

    }
    
}
