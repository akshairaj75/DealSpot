package com.backend.dealspot.serviceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backend.dealspot.dto.offer.OfferRequestDto;
import com.backend.dealspot.dto.offer.OfferResponseDto;
import com.backend.dealspot.entity.Category;
import com.backend.dealspot.entity.City;
import com.backend.dealspot.entity.Offer;
import com.backend.dealspot.entity.Product;
import com.backend.dealspot.entity.Store;
import com.backend.dealspot.repository.CategoryRepository;
import com.backend.dealspot.repository.CityRepository;
import com.backend.dealspot.repository.OfferRepository;
import com.backend.dealspot.repository.ProductRepository;
import com.backend.dealspot.repository.StoreRepository;
import com.backend.dealspot.security.CustomUserPrincipal;
import com.backend.dealspot.service.OfferService;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class OfferServiceImpl implements OfferService {

        @Autowired
        StoreRepository storeRepository;

        @Autowired
        ProductRepository productRepository;

        @Autowired
        CategoryRepository categoryRepository;

        @Autowired
        CityRepository cityRepository;

        @Autowired
        OfferRepository offerRepository;

        @Autowired
        FileStorageService fileStorageService;

        @Transactional
        @Override
        public OfferResponseDto addOffer(
                        OfferRequestDto dto,
                        CustomUserPrincipal authUser,
                        HttpServletRequest request) {

                Store store = storeRepository.findById(dto.getStoreId())
                                .orElseThrow(() -> new RuntimeException("store not found"));

                City city = cityRepository.findById(dto.getCityId())
                                .orElseThrow(() -> new RuntimeException("city not found"));

                Category category = categoryRepository.findById(dto.getCategoryId())
                                .orElseThrow(() -> new RuntimeException("category not found"));

                Product product = productRepository.findById(dto.getProductId())
                                .orElseThrow(() -> new RuntimeException("product not found"));

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

                offer.setImageUrl(dto.getImageUrl());
                offer.setThumbnailUrl(dto.getThumbnailUrl());

                offer.setValidFrom(dto.getValidFrom());
                offer.setValidUntil(dto.getValidUntil());

                offer.setFeatured(dto.getFeatured());
                offer.setFlash(dto.getFlash());
                offer.setOnline(dto.getOnline());
                offer.setInStore(dto.getInStore());
                offer.setActive(dto.getActive());

                offer.setSaveCount(0);
                offer.setShareCount(0);

                Offer savedOffer = offerRepository.save(offer);

                // if (dto.getImageUrl() != null) {
                // String folderName = "offer_images/" + savedOffer.getId();
                // String url = fileStorageService.storeFile(dto.getImageUrl(), folderName);
                // savedOffer.setImageUrl(url);
                // }
                // if (dto.getThumbnailUrl() != null) {
                // String folderName = "offer_thumbnails/" + savedOffer.getId();
                // String url = fileStorageService.storeFile(dto.getThumbnailUrl(), folderName);
                // savedOffer.setThumbnailUrl(url);
                // }

                return OfferResponseDto.fromEntity(savedOffer);

        }

        @Override
        public List<OfferResponseDto> fetchAllOffers() {

                List<Offer> offers = offerRepository.findAll();

                return offers.stream()
                                .map(OfferResponseDto::fromEntity).toList();
        }

}
