package com.backend.dealspot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.dealspot.dto.product.ProductRegisterDto;
import com.backend.dealspot.dto.product.ProductResponseDto;
import com.backend.dealspot.service.ProductService;

@RestController
@RequestMapping("/api/dealspot/products")
public class ProductController {

    @Autowired
    ProductService productService;

    @PostMapping("/add-product")
    public ResponseEntity<ProductResponseDto> registerProduct(ProductRegisterDto dto){
        
        ProductResponseDto res =  productService.registerProduct(dto);
        return ResponseEntity.ok(res);

    }
    
}
