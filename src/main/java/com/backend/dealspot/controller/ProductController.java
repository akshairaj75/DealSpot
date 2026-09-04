package com.backend.dealspot.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.backend.dealspot.dto.attributeKey.AttributeKeyDto;
import com.backend.dealspot.dto.attributeKey.AttributeKeyRegisterDto;
import com.backend.dealspot.dto.product.ProductDetailsDto;
import com.backend.dealspot.dto.product.ProductRegisterDto;
import com.backend.dealspot.dto.product.ProductResponseDto;
import com.backend.dealspot.service.ProductService;

@RestController
@RequestMapping("/api/dealspot/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('CONTENT_MANAGER')")
    @PostMapping("/add-key")
    public ResponseEntity<AttributeKeyRegisterDto> addAttributeKey(
            @RequestBody AttributeKeyRegisterDto dto) {

        return ResponseEntity.ok(productService.addAttributeKey(dto));
    }

    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('CONTENT_MANAGER')")
    @PostMapping("/add-key/bulk")
    public ResponseEntity<List<AttributeKeyRegisterDto>> addBulkAttributeKeys(
            @RequestBody List<AttributeKeyRegisterDto> dto) {

        List<AttributeKeyRegisterDto> res = dto.stream()
                .map(attributeKeyRegisterDto -> productService.addAttributeKey(attributeKeyRegisterDto))
                .toList();
        return ResponseEntity.ok(res);
    }
    // ======================================================================

    @GetMapping("/fetch-attribute-keys")
    public ResponseEntity<List<AttributeKeyDto>> fetchAttributeKeys() {

        return ResponseEntity.ok(productService.fetchAttributeKeys());
    }

    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('CONTENT_MANAGER')")
    @PostMapping("/add-product")
    public ResponseEntity<ProductResponseDto> registerProduct(
            @RequestPart("data") ProductRegisterDto dto,
            @RequestPart(value = "file", required = false) List<MultipartFile> file) {

        ProductResponseDto res = productService.registerProduct(dto, file);
        return ResponseEntity.ok(res);

    }

    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('CONTENT_MANAGER')")
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

    @GetMapping("/paged")
    public ResponseEntity<Page<ProductResponseDto>> getPagedProducts(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size,
            @RequestParam(name = "search", required = false) String search,
            @RequestParam(name = "categoryId", required = false) Integer categoryId,
            @RequestParam(name = "brandId", required = false) Long brandId,
            @RequestParam(name = "sortBy", defaultValue = "createdAt") String sortBy,
            @RequestParam(name = "direction", defaultValue = "desc") String direction) {
        Page<ProductResponseDto> result = productService.fetchPagedProducts(page, size, search, categoryId, brandId,
                sortBy, direction);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/fetch-product/{productId}")
    public ResponseEntity<ProductResponseDto> getProductById(
            @PathVariable("productId") Long productId) {
        ProductResponseDto result = productService.getProductById(productId);
        return ResponseEntity.ok(result);
    }

    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('CONTENT_MANAGER')")
    @PutMapping("/update-product/{productId}")
    public ResponseEntity<ProductResponseDto> editProduct(
            @PathVariable("productId") Long productId,
            @RequestPart("data") ProductRegisterDto dto,
            @RequestPart(value = "file", required = false) List<MultipartFile> file) {
        ProductResponseDto result = productService.editProduct(productId, dto, file);
        return ResponseEntity.ok(result);

    }

    @GetMapping("/get-product-details/{productId}")
    public ResponseEntity<List<ProductDetailsDto>> getProductDetails(
            @PathVariable("productId") Long productId) {
        List<ProductDetailsDto> productDetails = productService.getProductDetails(productId);
        return ResponseEntity.ok(productDetails);
    }

}
