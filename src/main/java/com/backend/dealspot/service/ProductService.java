package com.backend.dealspot.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.backend.dealspot.dto.product.ProductRegisterDto;
import com.backend.dealspot.dto.product.ProductResponseDto;

public interface ProductService {

    ProductResponseDto registerProduct(ProductRegisterDto dto, List<MultipartFile> file);

}
