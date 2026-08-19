package com.backend.dealspot.serviceImpl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
        product.setUnit(dto.getUnit());
        product.setDescriptionEn(dto.getDescriptionEn());
        product.setDescriptionAr(dto.getDescriptionAr());
        product.setUnitSize(dto.getUnitSize());
        product.setActive(dto.getActive());

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

            for (int i = 0; i < files.size(); i++) {
                MultipartFile file = files.get(i);
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

                    if (i == 0) {
                        savedProduct.setPrimaryImageUrl(filePath);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            savedProduct = productRepository.save(savedProduct);
        }

        return ProductResponseDto.fromEntity(savedProduct);
    }

    @Transactional
    @Override
    public AttributeKeyRegisterDto addAttributeKey(AttributeKeyRegisterDto dto) {

        if (attributeKeyRepository.existsByAttrKeyEnIgnoreCaseOrAttrKeyArIgnoreCase(dto.getAttrKeyEn(),
                dto.getAttrKeyAr())) {
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

    @Override
    public ProductResponseDto getProductById(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        return ProductResponseDto.fromEntity(product);
    }

    @Transactional
    @Override
    public ProductResponseDto editProduct(Long productId,
            ProductRegisterDto dto,
            List<MultipartFile> files) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (dto.getBrandId() != null) {
            Brand brand = brandRepository.findById(dto.getBrandId())
                    .orElseThrow(() -> new RuntimeException("Brand not found"));

            product.setBrand(brand);
        }

        // Category
        if (dto.getCategoryId() != null) {
            Category category = categoryRepository.findById(dto.getCategoryId().intValue())
                    .orElseThrow(() -> new RuntimeException("Category not found"));

            product.setCategory(category);
        }

        // Basic fields
        if (dto.getNameEn() != null) {
            product.setNameEn(dto.getNameEn());
        }

        if (dto.getNameAr() != null) {
            product.setNameAr(dto.getNameAr());
        }

        if (dto.getDescriptionEn() != null) {
            product.setDescriptionEn(dto.getDescriptionEn());
        }

        if (dto.getDescriptionAr() != null) {
            product.setDescriptionAr(dto.getDescriptionAr());
        }

        if (dto.getUnit() != null) {
            product.setUnit(dto.getUnit());
        }

        if (dto.getUnitSize() != null) {
            product.setUnitSize(dto.getUnitSize());
        }

        if (dto.getSku() != null) {
            product.setSku(dto.getSku());
        }

        if (dto.getBarcode() != null) {
            product.setBarcode(dto.getBarcode());
        }

        // Active status
        if (dto.getActive() != null) {
            product.setActive(dto.getActive());
        }

        /*
         * Replace all existing details
         */
        if (dto.getDetails() != null) {

            List<ProductDetail> existingDetails = new ArrayList<>(product.getDetails());

            productDetailRepository.deleteAll(existingDetails);
            product.getDetails().clear();

            for (ProductDetailsDto detailDto : dto.getDetails()) {

                AttributeKey attributeKey = attributeKeyRepository.findById(detailDto.getAttributeKeyId())
                        .orElseThrow(() -> new RuntimeException("Attribute Key not found"));

                ProductDetail detail = new ProductDetail();

                detail.setProduct(product);
                detail.setAttributeKey(attributeKey);
                detail.setAttrValueEn(detailDto.getAttrValueEn());
                detail.setAttrValueAr(detailDto.getAttrValueAr());
                detail.setSortOrder(
                        detailDto.getSortOrder() != null
                                ? detailDto.getSortOrder()
                                : 0);

                productDetailRepository.save(detail);

                product.getDetails().add(detail);
            }
        }

        /*
         * Replace all existing images
         */
        if (files != null && !files.isEmpty()) {

            List<ProductImage> existingImages = new ArrayList<>(product.getImages());

            for (ProductImage image : existingImages) {

                // Optional: delete physical file
                // fileStorageService.deleteFile(image.getImageUrl());

                productImageRepository.delete(image);
            }

            product.getImages().clear();

            for (int i = 0; i < files.size(); i++) {
                MultipartFile file = files.get(i);
                try {

                    String filePath = fileStorageService.storeFile(file, "products");

                    ProductImage image = new ProductImage();

                    image.setProduct(product);
                    image.setImageUrl(filePath);
                    image.setAltTextEn(file.getOriginalFilename());
                    image.setAltTextAr(file.getOriginalFilename());

                    productImageRepository.save(image);

                    product.getImages().add(image);

                    if (i == 0) {
                        product.setPrimaryImageUrl(filePath);
                    }

                } catch (IOException e) {

                    throw new RuntimeException(
                            "Failed to store file: "
                                    + file.getOriginalFilename(),
                            e);
                }
            }
        }

        Product updatedProduct = productRepository.save(product);

        return ProductResponseDto.fromEntity(updatedProduct);
    }

    @Override
    public List<ProductDetailsDto> getProductDetails(Long productId) {
        List<ProductDetail> details = productDetailRepository.findByProductId(productId);
        return details.stream()
                .map(ProductDetailsDto::fromEntity)
                .toList();
    }

}
