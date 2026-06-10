package com.backend.dealspot.service;

import com.backend.dealspot.dto.product.ProductRegisterDto;
import com.backend.dealspot.dto.product.ProductResponseDto;

public interface ProductService {

    ProductResponseDto registerProduct(ProductRegisterDto dto);

}
