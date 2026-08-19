package com.backend.dealspot.serviceImpl;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.backend.dealspot.dto.offer.OfferRequestDto;
import com.backend.dealspot.dto.offer.OfferResponseDto;
import com.backend.dealspot.entity.Category;
import com.backend.dealspot.entity.City;
import com.backend.dealspot.entity.Offer;
import com.backend.dealspot.entity.OfferImage;
import com.backend.dealspot.entity.Product;
import com.backend.dealspot.entity.Store;
import com.backend.dealspot.repository.CategoryRepository;
import com.backend.dealspot.repository.CityRepository;
import com.backend.dealspot.repository.OfferImageRepository;
import com.backend.dealspot.repository.OfferRepository;
import com.backend.dealspot.repository.ProductRepository;
import com.backend.dealspot.repository.StoreRepository;
import com.backend.dealspot.security.CustomUserPrincipal;
import com.backend.dealspot.service.OfferService;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class OfferServiceImpl implements OfferService {

        private final StoreRepository storeRepository;

        private final ProductRepository productRepository;

        private final CategoryRepository categoryRepository;

        private final CityRepository cityRepository;

        private final OfferRepository offerRepository;

        private final FileStorageService fileStorageService;

        private final OfferImageRepository offerImageRepository;

        public OfferServiceImpl(StoreRepository storeRepository, ProductRepository productRepository,
                        CategoryRepository categoryRepository, CityRepository cityRepository,
                        OfferRepository offerRepository, FileStorageService fileStorageService,
                        OfferImageRepository offerImageRepository) {
                this.storeRepository = storeRepository;
                this.productRepository = productRepository;
                this.categoryRepository = categoryRepository;
                this.cityRepository = cityRepository;
                this.offerRepository = offerRepository;
                this.fileStorageService = fileStorageService;
                this.offerImageRepository = offerImageRepository;
        }

        @Transactional
        @Override
        public OfferResponseDto addOffer(
                        OfferRequestDto dto,
                        List<MultipartFile> images,
                        CustomUserPrincipal authUser,
                        HttpServletRequest request) {

                Store store = storeRepository.findById(dto.getStoreId())
                                .orElseThrow(() -> new RuntimeException("Store not found"));

                City city = cityRepository.findById(dto.getCityId())
                                .orElseThrow(() -> new RuntimeException("City not found"));

                Category category = categoryRepository.findById(dto.getCategoryId())
                                .orElseThrow(() -> new RuntimeException("Category not found"));

                Product product = null;
                if (dto.getProductId() != null) {
                        product = productRepository.findById(dto.getProductId()).orElse(null);
                }

                Offer offer = new Offer();
                offer.setStore(store);
                offer.setCity(city);
                offer.setCategory(category);
                offer.setProduct(product);
                offer.setTitleEn(dto.getTitleEn());
                offer.setTitleAr(dto.getTitleAr());
                offer.setDescriptionEn(dto.getDescriptionEn());
                offer.setDescriptionAr(dto.getDescriptionAr());
                offer.setTermsEn(dto.getTermsEn());
                offer.setTermsAr(dto.getTermsAr());
                offer.setOriginalPrice(dto.getOriginalPrice());
                offer.setOfferPrice(dto.getOfferPrice());
                offer.setDiscountPct(dto.getDiscountPct());
                offer.setBadgeType(dto.getBadgeType());

                offer.setValidFrom(dto.getValidFrom());
                offer.setValidUntil(dto.getValidUntil());

                offer.setFeatured(dto.getFeatured() != null ? dto.getFeatured() : false);
                offer.setFlash(dto.getFlash() != null ? dto.getFlash() : false);
                offer.setOnline(dto.getOnline() != null ? dto.getOnline() : false);
                offer.setInStore(dto.getInStore() != null ? dto.getInStore() : true);
                offer.setActive(dto.getActive() != null ? dto.getActive() : true);

                offer.setViewCount(0L);
                offer.setSaveCount(0);
                offer.setShareCount(0);

                Offer savedOffer = offerRepository.save(offer);

                if (images != null && !images.isEmpty()) {
                        for (int i = 0; i < images.size(); i++) {
                                try {
                                        String filePath = fileStorageService.storeFile(
                                                        images.get(i),
                                                        "offers/offer-images");

                                        if (i == 0) {
                                                savedOffer.setImageUrl(filePath);
                                                savedOffer.setThumbnailUrl(filePath);
                                        }

                                        OfferImage offerImage = new OfferImage();
                                        offerImage.setOffer(savedOffer);
                                        offerImage.setImageUrl(filePath);

                                        offerImageRepository.save(offerImage);
                                        savedOffer.getImages().add(offerImage);

                                } catch (IOException e) {
                                        throw new RuntimeException("Failed to upload image", e);
                                }
                        }
                        savedOffer = offerRepository.save(savedOffer);
                } else if (product != null && product.getPrimaryImageUrl() != null && !product.getPrimaryImageUrl().trim().isEmpty()) {
                        // Fall back to product primary image if no offer image uploaded
                        savedOffer.setImageUrl(product.getPrimaryImageUrl());
                        savedOffer.setThumbnailUrl(product.getPrimaryImageUrl());
                        savedOffer = offerRepository.save(savedOffer);
                }

                return OfferResponseDto.fromEntity(savedOffer);
        }

        @Override
        public List<OfferResponseDto> fetchAllOffers() {
                List<Offer> offers = offerRepository.findAll();
                return offers.stream()
                                .map(OfferResponseDto::fromEntity).toList();
        }

        @Override
        public OfferResponseDto getOfferById(Long offerId) {
                Offer offer = offerRepository.findById(offerId)
                                .orElseThrow(() -> new RuntimeException("Offer not found"));
                return OfferResponseDto.fromEntity(offer);
        }

        @Transactional
        @Override
        public OfferResponseDto updateOffer(Long offerId, OfferRequestDto dto, List<MultipartFile> images,
                        CustomUserPrincipal authUser, HttpServletRequest request) {

                Offer offer = offerRepository.findById(offerId)
                                .orElseThrow(() -> new RuntimeException("Offer not found"));

                if (dto.getStoreId() != null) {
                        storeRepository.findById(dto.getStoreId()).ifPresent(offer::setStore);
                }
                if (dto.getCityId() != null) {
                        cityRepository.findById(dto.getCityId()).ifPresent(offer::setCity);
                }
                if (dto.getCategoryId() != null) {
                        categoryRepository.findById(dto.getCategoryId()).ifPresent(offer::setCategory);
                }
                if (dto.getProductId() != null) {
                        offer.setProduct(productRepository.findById(dto.getProductId()).orElse(null));
                } else {
                        offer.setProduct(null);
                }

                offer.setTitleEn(dto.getTitleEn());
                offer.setTitleAr(dto.getTitleAr());
                offer.setDescriptionEn(dto.getDescriptionEn());
                offer.setDescriptionAr(dto.getDescriptionAr());
                offer.setTermsEn(dto.getTermsEn());
                offer.setTermsAr(dto.getTermsAr());
                offer.setOriginalPrice(dto.getOriginalPrice());
                offer.setOfferPrice(dto.getOfferPrice());
                offer.setDiscountPct(dto.getDiscountPct());
                offer.setBadgeType(dto.getBadgeType());

                offer.setValidFrom(dto.getValidFrom());
                offer.setValidUntil(dto.getValidUntil());

                if (dto.getFeatured() != null)
                        offer.setFeatured(dto.getFeatured());
                if (dto.getFlash() != null)
                        offer.setFlash(dto.getFlash());
                if (dto.getOnline() != null)
                        offer.setOnline(dto.getOnline());
                if (dto.getInStore() != null)
                        offer.setInStore(dto.getInStore());
                if (dto.getActive() != null)
                        offer.setActive(dto.getActive());

                if (images != null && !images.isEmpty()) {
                        for (int i = 0; i < images.size(); i++) {
                                try {
                                        String filePath = fileStorageService.storeFile(
                                                        images.get(i),
                                                        "offers/offer-images");

                                        if (i == 0) {
                                                offer.setImageUrl(filePath);
                                                offer.setThumbnailUrl(filePath);
                                        }

                                        OfferImage offerImage = new OfferImage();
                                        offerImage.setOffer(offer);
                                        offerImage.setImageUrl(filePath);

                                        offerImageRepository.save(offerImage);
                                        offer.getImages().add(offerImage);

                                } catch (IOException e) {
                                        throw new RuntimeException("Failed to upload image", e);
                                }
                        }
                } else if ((offer.getImageUrl() == null || offer.getImageUrl().trim().isEmpty()) &&
                                offer.getProduct() != null && offer.getProduct().getPrimaryImageUrl() != null &&
                                !offer.getProduct().getPrimaryImageUrl().trim().isEmpty()) {
                        offer.setImageUrl(offer.getProduct().getPrimaryImageUrl());
                        offer.setThumbnailUrl(offer.getProduct().getPrimaryImageUrl());
                }

                Offer savedOffer = offerRepository.save(offer);
                return OfferResponseDto.fromEntity(savedOffer);
        }

        @Transactional
        @Override
        public void deleteOffer(Long offerId) {
                Offer offer = offerRepository.findById(offerId)
                                .orElseThrow(() -> new RuntimeException("Offer not found"));
                offerImageRepository.deleteAll(offer.getImages());
                offerRepository.delete(offer);
        }

}
