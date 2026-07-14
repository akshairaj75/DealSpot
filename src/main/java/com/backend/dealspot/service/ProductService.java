package com.backend.dealspot.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.backend.dealspot.dto.attributeKey.AttributeKeyDto;
import com.backend.dealspot.dto.attributeKey.AttributeKeyRegisterDto;
import com.backend.dealspot.dto.product.ProductDetailsDto;
import com.backend.dealspot.dto.product.ProductRegisterDto;
import com.backend.dealspot.dto.product.ProductResponseDto;

public interface ProductService {

    ProductResponseDto registerProduct(ProductRegisterDto dto, List<MultipartFile> file);

    AttributeKeyRegisterDto addAttributeKey(AttributeKeyRegisterDto dto);

   
    List<AttributeKeyDto> fetchAttributeKeys();

    List<ProductResponseDto> fetchAllProducts();

    ProductResponseDto editProduct(Long productId, ProductRegisterDto dto, List<MultipartFile> file);

    ProductResponseDto getProductById(Long productId);

    List<ProductDetailsDto> getProductDetails(Long productId);

}
