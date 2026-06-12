package com.backend.dealspot.service.serviceImpl;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.backend.dealspot.dto.product.ProductRegisterDto;
import com.backend.dealspot.dto.product.ProductResponseDto;
import com.backend.dealspot.dto.attributeKey.AttributeKeyDto;
import com.backend.dealspot.dto.attributeKey.AttributeKeyRegisterDto;
import com.backend.dealspot.dto.product.ProductDetailsDto;
import com.backend.dealspot.entity.AttributeKey;
import com.backend.dealspot.entity.Brand;
import com.backend.dealspot.entity.Category;
import com.backend.dealspot.entity.Product;
import com.backend.dealspot.entity.ProductDetail;
import com.backend.dealspot.entity.ProductImage;
import com.backend.dealspot.repository.AttributeKeyRepository;
import com.backend.dealspot.repository.BrandRepository;
import com.backend.dealspot.repository.CategoryRepository;
import com.backend.dealspot.repository.ProductRepository;
import com.backend.dealspot.repository.ProductDetailRepository;
import com.backend.dealspot.repository.ProductImageRepository;
import com.backend.dealspot.service.ProductService;

import jakarta.transaction.Transactional;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final FileStorageService fileStorageService;
    private final ProductDetailRepository productDetailRepository;
    private final ProductImageRepository productImageRepository;
    private final BrandRepository brandRepository;
    private final AttributeKeyRepository attributeKeyRepository;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository,
            CategoryRepository categoryRepository,
            FileStorageService fileStorageService,
            ProductDetailRepository productDetailRepository,
            ProductImageRepository productImageRepository,
            BrandRepository brandRepository,
            AttributeKeyRepository attributeKeyRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.fileStorageService = fileStorageService;
        this.productDetailRepository = productDetailRepository;
        this.productImageRepository = productImageRepository;
        this.brandRepository = brandRepository;
        this.attributeKeyRepository = attributeKeyRepository;
    }

    @Transactional
    @Override
    public ProductResponseDto registerProduct(ProductRegisterDto dto, List<MultipartFile> files) {
        Product product = new Product();

        if (dto.getCategoryId() != null) {
            Category category = categoryRepository.findById(dto.getCategoryId().intValue())
                    .orElseThrow(() -> new RuntimeException("Category not found"));
            product.setCategory(category);
        }

        if (dto.getBrandId() != null) {
            Brand brand = brandRepository.findById(dto.getBrandId())
                    .orElseThrow(() -> new RuntimeException("Brand not found"));
            product.setBrand(brand);
        }
        product.setSku(dto.getSku());
        product.setBarcode(dto.getBarcode());
        product.setNameEn(dto.getNameEn());
        product.setNameAr(dto.getNameAr());
        product.setDescriptionEn(dto.getDescriptionEn());
        product.setDescriptionAr(dto.getDescriptionAr());
        product.setUnitSize(dto.getUnitSize());
        product.setActive(dto.isActive());

        Product savedProduct = productRepository.save(product);

        if (dto.getDetails() != null && !dto.getDetails().isEmpty()) {
            for (ProductDetailsDto detailDto : dto.getDetails()) {

                AttributeKey attributeKey = attributeKeyRepository.findById(detailDto.getAttributeKeyId())
                        .orElseThrow(() -> new RuntimeException("Attribute Key not found"));

                ProductDetail detail = new ProductDetail();
                detail.setProduct(savedProduct);
                detail.setAttributeKey(attributeKey);
                detail.setAttrValueEn(detailDto.getAttrValueEn());
                detail.setAttrValueAr(detailDto.getAttrValueAr());
                detail.setSortOrder(detailDto.getSortOrder() != null ? detailDto.getSortOrder() : 0);
                productDetailRepository.save(detail);
                savedProduct.getDetails().add(detail);
            }
        }

        if (files != null && !files.isEmpty()) {

            for (MultipartFile file : files) {

                String filePath;
                ProductImage image = new ProductImage();
                image.setProduct(savedProduct);
                try {
                    filePath = fileStorageService.storeFile(file, "products");
                    image.setImageUrl(filePath);
                    image.setAltTextEn(file.getOriginalFilename());
                    image.setAltTextAr(file.getOriginalFilename());
                    productImageRepository.save(image);
                    savedProduct.getImages().add(image);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }

        return ProductResponseDto.fromEntity(savedProduct);
    }

    @Override
    public AttributeKeyRegisterDto addAttributeKey(AttributeKeyRegisterDto dto) {

        if (attributeKeyRepository.existsByAttrKeyEnIgnoreCaseOrAttrKeyArIgnoreCase(dto.getAttrKeyEn(), dto.getAttrKeyAr())) {
            throw new IllegalArgumentException("Category with the same English or Arabic name already exists");
        }
        AttributeKey attributeKey = new AttributeKey();
        attributeKey.setAttrKeyEn(dto.getAttrKeyEn());
        attributeKey.setAttrKeyAr(dto.getAttrKeyAr());
        AttributeKey savedKey = attributeKeyRepository.save(attributeKey);
        return AttributeKeyRegisterDto.fromEntity(savedKey);
    }

    @Override
    public List<AttributeKeyDto> fetchAttributeKeys() {
        List<AttributeKey> keys = attributeKeyRepository.findAll();
        return keys.stream()
                .map(AttributeKeyDto::fromEntity)
                .toList();
    }

    @Override
    public List<ProductResponseDto> fetchAllProducts() {
        List<Product> result = productRepository.findAll();

        return result.stream()
                .map(ProductResponseDto::fromEntity)
                .toList();
    }

}
