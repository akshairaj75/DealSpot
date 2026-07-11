package com.backend.dealspot.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.backend.dealspot.dto.attributeKey.AttributeKeyDto;
import com.backend.dealspot.dto.attributeKey.AttributeKeyRegisterDto;
import com.backend.dealspot.dto.product.ProductRegisterDto;
import com.backend.dealspot.dto.product.ProductResponseDto;
import com.backend.dealspot.service.ProductService;

@RestController
@RequestMapping("/api/dealspot/products")
public class ProductController {

    @Autowired
    ProductService productService;

    @PostMapping("/add-key")
    public ResponseEntity<AttributeKeyRegisterDto> addAttributeKey(
            @RequestBody AttributeKeyRegisterDto dto) {

        return ResponseEntity.ok(productService.addAttributeKey(dto));
    }

    @PostMapping("/add-key/bulk")
    public ResponseEntity<List<AttributeKeyRegisterDto>> addBulkAttributeKeys(
            @RequestBody List<AttributeKeyRegisterDto> dto) {

        List<AttributeKeyRegisterDto> res = dto.stream()
                .map(attributeKeyRegisterDto -> productService.addAttributeKey(attributeKeyRegisterDto))
                .toList();
        return ResponseEntity.ok(res);
    }

    @GetMapping("/fetch-attribute-keys")
    public ResponseEntity<List<AttributeKeyDto>> fetchAttributeKeys() {

        return ResponseEntity.ok(productService.fetchAttributeKeys());
    }

    @PostMapping("/add-product")
    public ResponseEntity<ProductResponseDto> registerProduct(
            @RequestPart("data") ProductRegisterDto dto,
            @RequestPart(value = "file", required = false) List<MultipartFile> file) {

        ProductResponseDto res = productService.registerProduct(dto, file);
        return ResponseEntity.ok(res);

    }

    @PostMapping("/add-product/bulk")
    public ResponseEntity<List<ProductResponseDto>> addBulkProducts(
            @RequestBody List<ProductRegisterDto> dto,
            @RequestPart(value = "file", required = false) List<MultipartFile> file) {

        List<ProductResponseDto> res = dto.stream()
                .map(productRegisterDto -> productService.registerProduct(productRegisterDto, file))
                .toList();
        return ResponseEntity.ok(res);
    }

    @GetMapping("/fetch-all-products")
    public ResponseEntity<List<ProductResponseDto>> fetchAllProducts() {
        List<ProductResponseDto> result = productService.fetchAllProducts();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/fetch-product/{productId}")
    public ResponseEntity<ProductResponseDto> getProductById(
            @PathVariable("productId") Long productId) {
        ProductResponseDto result = productService.getProductById(productId);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/update-product/{productId}")
    public ResponseEntity<ProductResponseDto> editProduct(
            @PathVariable("productId") Long productId,
            @RequestPart("data") ProductRegisterDto dto,
            @RequestPart(value = "file", required = false) List<MultipartFile> file) {
        ProductResponseDto result = productService.editProduct(productId, dto, file);
        return ResponseEntity.ok(result);

    }

}
