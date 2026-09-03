package com.backend.dealspot.serviceImpl;

import java.io.IOException;
import java.util.List;

import org.springframework.security.access.AccessDeniedException;
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
import com.backend.dealspot.enums.AdminRole;
import com.backend.dealspot.enums.OfferBadgeType;
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

                if (authUser != null && authUser.getRole() == AdminRole.STORE_MANAGER) {
                        if (authUser.getStoreId() == null) {
                                throw new AccessDeniedException("No store is assigned to this store manager account");
                        }
                        if (dto.getStoreId() != null && !dto.getStoreId().equals(authUser.getStoreId().longValue())) {
                                throw new AccessDeniedException("Store managers can only create offers for their own store and branches");
                        }
                        dto.setStoreId(authUser.getStoreId().longValue());
                }

                Store store = storeRepository.findById(dto.getStoreId().intValue())
                                .orElseThrow(() -> new RuntimeException("Store not found"));


                City city = cityRepository.findById(dto.getCityId().intValue())
                                .orElseThrow(() -> new RuntimeException("City not found"));

                Category category = categoryRepository.findById(dto.getCategoryId().intValue())
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
                } else if (product != null) {
                        String pImg = product.getPrimaryImageUrl();
                        if ((pImg == null || pImg.trim().isEmpty()) && product.getImages() != null && !product.getImages().isEmpty()) {
                                pImg = product.getImages().get(0).getImageUrl();
                        }
                        if (pImg != null && !pImg.trim().isEmpty()) {
                                savedOffer.setImageUrl(pImg);
                                savedOffer.setThumbnailUrl(pImg);
                                savedOffer = offerRepository.save(savedOffer);
                        }
                }

                return OfferResponseDto.fromEntity(savedOffer);
        }

        @Override
        public List<OfferResponseDto> fetchAllOffers(CustomUserPrincipal authUser, Integer storeId, Boolean includeExpired) {
                boolean isAdminOrManager = authUser != null && (
                        authUser.getRole() == AdminRole.SUPER_ADMIN || 
                        authUser.getRole() == AdminRole.STORE_MANAGER ||
                        authUser.getRole() == AdminRole.CONTENT_MANAGER
                );

                boolean shouldIncludeExpired = Boolean.TRUE.equals(includeExpired) || isAdminOrManager;
                java.time.LocalDate today = java.time.LocalDate.now();
                List<Offer> offers;

                if (authUser != null && authUser.getRole() == AdminRole.STORE_MANAGER && authUser.getStoreId() != null) {
                        offers = shouldIncludeExpired 
                                ? offerRepository.findByStoreId(authUser.getStoreId())
                                : offerRepository.findActiveAndValidOffersByStoreId(authUser.getStoreId(), today);
                } else if (storeId != null) {
                        offers = shouldIncludeExpired
                                ? offerRepository.findByStoreId(storeId)
                                : offerRepository.findActiveAndValidOffersByStoreId(storeId, today);
                } else {
                        offers = shouldIncludeExpired
                                ? offerRepository.findAll()
                                : offerRepository.findActiveAndValidOffers(today);
                }

                return offers.stream()
                                .map(OfferResponseDto::fromEntity)
                                .toList();
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

                if (authUser != null && authUser.getRole() == AdminRole.STORE_MANAGER) {
                        if (authUser.getStoreId() == null) {
                                throw new AccessDeniedException("No store is assigned to this store manager account");
                        }
                        if (offer.getStore() == null || !offer.getStore().getId().equals(authUser.getStoreId())) {
                                throw new AccessDeniedException("You are not authorized to update offers for another store");
                        }
                        if (dto.getStoreId() != null && !dto.getStoreId().equals(authUser.getStoreId().longValue())) {
                                throw new AccessDeniedException("Store managers cannot transfer an offer to another store");
                        }
                        dto.setStoreId(authUser.getStoreId().longValue());
                }

                if (dto.getStoreId() != null) {
                        storeRepository.findById(dto.getStoreId().intValue()).ifPresent(offer::setStore);
                }
                if (dto.getCityId() != null) {
                        cityRepository.findById(dto.getCityId().intValue()).ifPresent(offer::setCity);
                }
                if (dto.getCategoryId() != null) {
                        categoryRepository.findById(dto.getCategoryId().intValue()).ifPresent(offer::setCategory);
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

                if (dto.getBadgeType() == OfferBadgeType.FEATURED) {
                        offer.setFeatured(true);
                } else if (dto.getBadgeType() == OfferBadgeType.FLASH) {
                        offer.setFlash(true);
                }

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
                } else if ((offer.getImageUrl() == null || offer.getImageUrl().trim().isEmpty()) && offer.getProduct() != null) {
                        String pImg = offer.getProduct().getPrimaryImageUrl();
                        if ((pImg == null || pImg.trim().isEmpty()) && offer.getProduct().getImages() != null && !offer.getProduct().getImages().isEmpty()) {
                                pImg = offer.getProduct().getImages().get(0).getImageUrl();
                        }
                        if (pImg != null && !pImg.trim().isEmpty()) {
                                offer.setImageUrl(pImg);
                                offer.setThumbnailUrl(pImg);
                        }
                }

                Offer savedOffer = offerRepository.save(offer);
                return OfferResponseDto.fromEntity(savedOffer);
        }

        @Transactional
        @Override
        public void deleteOffer(Long offerId, CustomUserPrincipal authUser) {
                Offer offer = offerRepository.findById(offerId)
                                .orElseThrow(() -> new RuntimeException("Offer not found"));

                if (authUser != null && authUser.getRole() == AdminRole.STORE_MANAGER) {
                        if (offer.getStore() == null || !offer.getStore().getId().equals(authUser.getStoreId())) {
                                throw new AccessDeniedException("You are not authorized to delete offers for another store");
                        }
                }

                offerImageRepository.deleteAll(offer.getImages());
                offerRepository.delete(offer);
        }

        @Transactional
        @Override
        public OfferResponseDto extendOffer(Long offerId, int days, CustomUserPrincipal authUser) {
                Offer offer = offerRepository.findById(offerId)
                                .orElseThrow(() -> new RuntimeException("Offer not found"));

                if (authUser != null && authUser.getRole() == AdminRole.STORE_MANAGER) {
                        if (offer.getStore() == null || !offer.getStore().getId().equals(authUser.getStoreId())) {
                                throw new AccessDeniedException("You are not authorized to extend offers for another store");
                        }
                }

                java.time.LocalDate baseDate = java.time.LocalDate.now();
                if (offer.getValidUntil() != null && offer.getValidUntil().isAfter(baseDate)) {
                        baseDate = offer.getValidUntil();
                }

                offer.setValidUntil(baseDate.plusDays(days));
                offer.setActive(true);

                Offer updated = offerRepository.save(offer);
                return OfferResponseDto.fromEntity(updated);
        }

        @Override
        public org.springframework.data.domain.Page<OfferResponseDto> getPagedOffers(
                        String search,
                        Integer storeId,
                        String badgeType,
                        Boolean active,
                        int page,
                        int size) {

                org.springframework.data.domain.Pageable pageable = 
                        org.springframework.data.domain.PageRequest.of(page, size, org.springframework.data.domain.Sort.by("id").descending());

                com.backend.dealspot.enums.OfferBadgeType badgeEnum = null;
                if (badgeType != null && !badgeType.trim().isEmpty() && !"ALL".equalsIgnoreCase(badgeType)) {
                        try {
                                badgeEnum = com.backend.dealspot.enums.OfferBadgeType.valueOf(badgeType.trim().toUpperCase());
                        } catch (IllegalArgumentException e) {
                                // invalid badge type enum, ignore
                        }
                }

                String searchQuery = (search != null && !search.trim().isEmpty()) ? search.trim() : null;

                org.springframework.data.domain.Page<Offer> offersPage = offerRepository.searchOffers(
                                searchQuery,
                                storeId,
                                badgeEnum,
                                active,
                                pageable);

                return offersPage.map(OfferResponseDto::fromEntity);
        }
}

