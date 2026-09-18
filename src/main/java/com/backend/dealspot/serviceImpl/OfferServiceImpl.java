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
import com.backend.dealspot.enums.AuditAction;
import com.backend.dealspot.enums.OfferBadgeType;
import com.backend.dealspot.repository.CategoryRepository;
import com.backend.dealspot.repository.CityRepository;
import com.backend.dealspot.repository.OfferImageRepository;
import com.backend.dealspot.repository.OfferRepository;
import com.backend.dealspot.repository.ProductRepository;
import com.backend.dealspot.repository.SpecialOfferRepository;
import com.backend.dealspot.repository.StoreRepository;
import com.backend.dealspot.security.CustomUserPrincipal;
import com.backend.dealspot.service.AuditLogService;
import com.backend.dealspot.service.OfferService;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class OfferServiceImpl implements OfferService {

        private final StoreRepository storeRepository;
        private final ProductRepository productRepository;
        private final CategoryRepository categoryRepository;
        private final CityRepository cityRepository;
        private final OfferRepository offerRepository;
        private final SpecialOfferRepository specialOfferRepository;
        private final FileStorageService fileStorageService;
        private final OfferImageRepository offerImageRepository;
        private final AuditLogService auditLogService;

        public OfferServiceImpl(StoreRepository storeRepository, ProductRepository productRepository,
                        CategoryRepository categoryRepository, CityRepository cityRepository,
                        OfferRepository offerRepository, SpecialOfferRepository specialOfferRepository,
                        FileStorageService fileStorageService, OfferImageRepository offerImageRepository,
                        AuditLogService auditLogService) {
                this.storeRepository = storeRepository;
                this.productRepository = productRepository;
                this.categoryRepository = categoryRepository;
                this.cityRepository = cityRepository;
                this.offerRepository = offerRepository;
                this.specialOfferRepository = specialOfferRepository;
                this.fileStorageService = fileStorageService;
                this.offerImageRepository = offerImageRepository;
                this.auditLogService = auditLogService;
        }

        private void validateNoOverlappingOffers(Long productId, Integer storeId, java.time.LocalDate validFrom, java.time.LocalDate validUntil, Long excludeOfferId) {
                if (productId == null || storeId == null || validFrom == null || validUntil == null) {
                        return;
                }
                List<Offer> overlaps;
                if (excludeOfferId != null) {
                        overlaps = offerRepository.findOverlappingActiveOffersExcluding(productId, storeId, validFrom, validUntil, excludeOfferId);
                } else {
                        overlaps = offerRepository.findOverlappingActiveOffers(productId, storeId, validFrom, validUntil);
                }
                if (!overlaps.isEmpty()) {
                        Offer conflicting = overlaps.get(0);
                        throw new com.backend.dealspot.exception.OfferConflictException(
                                        "An active offer already exists for this product at this store between " +
                                                        conflicting.getValidFrom() + " and " + conflicting.getValidUntil() +
                                                        " (Offer ID: " + conflicting.getId() + ", Price: " + conflicting.getOfferPrice() + ")",
                                        conflicting);
                }
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
                                throw new AccessDeniedException(
                                                "Store managers can only create offers for their own store and branches");
                        }
                        dto.setStoreId(authUser.getStoreId().longValue());
                }

                Store store = storeRepository.findById(dto.getStoreId().intValue())
                                .orElseThrow(() -> new RuntimeException("Store not found"));

                City city = cityRepository.findById(dto.getCityId().intValue())
                                .orElseThrow(() -> new RuntimeException("City not found"));

                Category category = null;
                if (dto.getCategoryId() != null) {
                        category = categoryRepository.findById(dto.getCategoryId().intValue()).orElse(null);
                }

                Product product = null;
                if (dto.getProductId() != null) {
                        product = productRepository.findByIdForUpdate(dto.getProductId()).orElse(null);
                        if (category == null && product != null && product.getCategory() != null) {
                                category = product.getCategory();
                        }
                }

                if (category == null) {
                        throw new RuntimeException("Category not found");
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

                if (offer.isActive() && product != null) {
                        validateNoOverlappingOffers(product.getId(), store.getId(), offer.getValidFrom(), offer.getValidUntil(), null);
                }

                if (dto.getSpecialOfferId() != null) {
                        specialOfferRepository.findById(dto.getSpecialOfferId()).ifPresent(offer::setSpecialOffer);
                }

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
                        if ((pImg == null || pImg.trim().isEmpty()) && product.getImages() != null
                                        && !product.getImages().isEmpty()) {
                                pImg = product.getImages().get(0).getImageUrl();
                        }
                        if (pImg != null && !pImg.trim().isEmpty()) {
                                savedOffer.setImageUrl(pImg);
                                savedOffer.setThumbnailUrl(pImg);
                                savedOffer = offerRepository.save(savedOffer);
                        }
                }

                java.util.Map<String, Object> auditPayload = new java.util.HashMap<>();
                auditPayload.put("titleEn", savedOffer.getTitleEn());
                auditPayload.put("titleAr", savedOffer.getTitleAr());
                if (savedOffer.getStore() != null) {
                        auditPayload.put("storeId", savedOffer.getStore().getId());
                        auditPayload.put("storeNameEn", savedOffer.getStore().getNameEn());
                }
                auditPayload.put("offerPrice", savedOffer.getOfferPrice());
                auditLogService.logAction("OFFER", savedOffer.getId(), authUser, AuditAction.CREATE, auditPayload,
                                request);

                return OfferResponseDto.fromEntity(savedOffer);
        }

        @Override
        public List<OfferResponseDto> fetchAllOffers(CustomUserPrincipal authUser, Integer storeId,
                        Boolean includeExpired) {
                boolean shouldIncludeExpired = Boolean.TRUE.equals(includeExpired);
                java.time.LocalDate today = java.time.LocalDate.now();
                List<Offer> offers;

                if (authUser != null && authUser.getRole() == AdminRole.STORE_MANAGER && authUser.getStoreId() != null
                                && shouldIncludeExpired) {
                        offers = offerRepository.findByStoreId(authUser.getStoreId());
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
                                throw new AccessDeniedException(
                                                "You are not authorized to update offers for another store");
                        }
                        if (dto.getStoreId() != null && !dto.getStoreId().equals(authUser.getStoreId().longValue())) {
                                throw new AccessDeniedException(
                                                "Store managers cannot transfer an offer to another store");
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
                        offer.setProduct(productRepository.findByIdForUpdate(dto.getProductId()).orElse(null));
                } else if (offer.getProduct() != null) {
                        productRepository.findByIdForUpdate(offer.getProduct().getId());
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

                if (dto.getSpecialOfferId() != null) {
                        specialOfferRepository.findById(dto.getSpecialOfferId()).ifPresent(offer::setSpecialOffer);
                } else {
                        offer.setSpecialOffer(null);
                }

                if (offer.isActive() && offer.getProduct() != null && offer.getStore() != null) {
                        validateNoOverlappingOffers(offer.getProduct().getId(), offer.getStore().getId(), offer.getValidFrom(), offer.getValidUntil(), offer.getId());
                }

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
                } else if ((offer.getImageUrl() == null || offer.getImageUrl().trim().isEmpty())
                                && offer.getProduct() != null) {
                        String pImg = offer.getProduct().getPrimaryImageUrl();
                        if ((pImg == null || pImg.trim().isEmpty()) && offer.getProduct().getImages() != null
                                        && !offer.getProduct().getImages().isEmpty()) {
                                pImg = offer.getProduct().getImages().get(0).getImageUrl();
                        }
                        if (pImg != null && !pImg.trim().isEmpty()) {
                                offer.setImageUrl(pImg);
                                offer.setThumbnailUrl(pImg);
                        }
                }

                Offer savedOffer = offerRepository.save(offer);

                java.util.Map<String, Object> auditPayload = new java.util.HashMap<>();
                auditPayload.put("titleEn", savedOffer.getTitleEn());
                auditPayload.put("titleAr", savedOffer.getTitleAr());
                if (savedOffer.getStore() != null) {
                        auditPayload.put("storeId", savedOffer.getStore().getId());
                        auditPayload.put("storeNameEn", savedOffer.getStore().getNameEn());
                }
                auditPayload.put("offerPrice", savedOffer.getOfferPrice());
                auditLogService.logAction("OFFER", savedOffer.getId(), authUser, AuditAction.UPDATE, auditPayload,
                                request);

                return OfferResponseDto.fromEntity(savedOffer);
        }

        @Transactional
        @Override
        public void deleteOffer(Long offerId, CustomUserPrincipal authUser) {
                Offer offer = offerRepository.findById(offerId)
                                .orElseThrow(() -> new RuntimeException("Offer not found"));

                if (authUser != null && authUser.getRole() == AdminRole.STORE_MANAGER) {
                        if (offer.getStore() == null || !offer.getStore().getId().equals(authUser.getStoreId())) {
                                throw new AccessDeniedException(
                                                "You are not authorized to delete offers for another store");
                        }
                }

                java.util.Map<String, Object> auditPayload = new java.util.HashMap<>();
                auditPayload.put("titleEn", offer.getTitleEn());
                auditPayload.put("titleAr", offer.getTitleAr());
                if (offer.getStore() != null) {
                        auditPayload.put("storeId", offer.getStore().getId());
                }

                offerImageRepository.deleteAll(offer.getImages());
                offerRepository.delete(offer);

                auditLogService.logAction("OFFER", offerId, authUser, AuditAction.DELETE, auditPayload, null);
        }

        @Transactional
        @Override
        public OfferResponseDto extendOffer(Long offerId, int days, CustomUserPrincipal authUser) {
                Offer offer = offerRepository.findById(offerId)
                                .orElseThrow(() -> new RuntimeException("Offer not found"));

                if (authUser != null && authUser.getRole() == AdminRole.STORE_MANAGER) {
                        if (offer.getStore() == null || !offer.getStore().getId().equals(authUser.getStoreId())) {
                                throw new AccessDeniedException(
                                                "You are not authorized to extend offers for another store");
                        }
                }

                java.time.LocalDate baseDate = java.time.LocalDate.now();
                if (offer.getValidUntil() != null && offer.getValidUntil().isAfter(baseDate)) {
                        baseDate = offer.getValidUntil();
                }

                java.time.LocalDate newValidUntil = baseDate.plusDays(days);
                if (offer.getProduct() != null && offer.getStore() != null) {
                        productRepository.findByIdForUpdate(offer.getProduct().getId());
                        validateNoOverlappingOffers(offer.getProduct().getId(), offer.getStore().getId(), offer.getValidFrom(), newValidUntil, offer.getId());
                }

                offer.setValidUntil(newValidUntil);
                offer.setActive(true);

                Offer updated = offerRepository.save(offer);

                java.util.Map<String, Object> auditPayload = new java.util.HashMap<>();
                auditPayload.put("titleEn", updated.getTitleEn());
                auditPayload.put("extendedDays", days);
                auditPayload.put("validUntil",
                                updated.getValidUntil() != null ? updated.getValidUntil().toString() : "");
                auditLogService.logAction("OFFER", updated.getId(), authUser, AuditAction.UPDATE, auditPayload, null);

                return OfferResponseDto.fromEntity(updated);
        }

        @Transactional
        @Override
        public OfferResponseDto splitAndCreateOffer(
                        com.backend.dealspot.dto.offer.OfferPeriodSplitRequestDto dto,
                        CustomUserPrincipal authUser,
                        HttpServletRequest request) {

                Offer existing = offerRepository.findById(dto.getExistingOfferId())
                                .orElseThrow(() -> new IllegalArgumentException("Existing offer not found with ID: " + dto.getExistingOfferId()));

                if (authUser != null && authUser.getRole() == AdminRole.STORE_MANAGER) {
                        if (existing.getStore() == null || !existing.getStore().getId().equals(authUser.getStoreId())) {
                                throw new AccessDeniedException("You are not authorized to modify offers for another store");
                        }
                }

                if (existing.getProduct() == null) {
                        throw new IllegalArgumentException("Cannot split an offer that does not have an associated product");
                }

                Long productId = existing.getProduct().getId();
                Integer storeId = existing.getStore().getId();

                // Concurrency protection: lock product
                productRepository.findByIdForUpdate(productId);

                java.time.LocalDate splitFrom = dto.getSplitValidFrom();
                java.time.LocalDate splitUntil = dto.getSplitValidUntil();

                if (splitUntil.isBefore(splitFrom)) {
                        throw new IllegalArgumentException("Split end date cannot be before split start date");
                }

                // Verify that the split range does NOT collide with any OTHER active offer for this product & store
                List<Offer> otherOverlaps = offerRepository.findOverlappingActiveOffersExcluding(
                                productId, storeId, splitFrom, splitUntil, existing.getId());
                if (!otherOverlaps.isEmpty()) {
                        Offer conflicting = otherOverlaps.get(0);
                        throw new com.backend.dealspot.exception.OfferConflictException(
                                        "Split period conflicts with another active offer (ID: " + conflicting.getId() + ")",
                                        conflicting);
                }

                java.time.LocalDate origFrom = existing.getValidFrom();
                java.time.LocalDate origUntil = existing.getValidUntil();

                if (splitFrom.isBefore(origFrom) || splitUntil.isAfter(origUntil)) {
                        throw new IllegalArgumentException("Split period (" + splitFrom + " to " + splitUntil + ") must fall within the existing offer validity period (" + origFrom + " to " + origUntil + ")");
                }

                com.backend.dealspot.entity.SpecialOffer specialOffer = null;
                if (dto.getSpecialOfferId() != null) {
                        specialOffer = specialOfferRepository.findById(dto.getSpecialOfferId())
                                        .orElseThrow(() -> new IllegalArgumentException("Special offer not found with ID: " + dto.getSpecialOfferId()));
                }

                java.math.BigDecimal originalPrice = dto.getNewOriginalPrice() != null ? dto.getNewOriginalPrice() : existing.getOriginalPrice();
                int discountPct = 0;
                if (originalPrice != null && originalPrice.compareTo(java.math.BigDecimal.ZERO) > 0 && dto.getNewOfferPrice() != null) {
                        java.math.BigDecimal diff = originalPrice.subtract(dto.getNewOfferPrice());
                        if (diff.compareTo(java.math.BigDecimal.ZERO) > 0) {
                                discountPct = diff.multiply(java.math.BigDecimal.valueOf(100))
                                                .divide(originalPrice, 0, java.math.RoundingMode.HALF_UP).intValue();
                        }
                }

                Offer campaignOffer = new Offer();
                campaignOffer.setStore(existing.getStore());
                campaignOffer.setCity(existing.getCity());
                campaignOffer.setCategory(existing.getCategory());
                campaignOffer.setProduct(existing.getProduct());
                campaignOffer.setTitleEn(dto.getTitleEn() != null && !dto.getTitleEn().trim().isEmpty() ? dto.getTitleEn() : existing.getTitleEn());
                campaignOffer.setTitleAr(dto.getTitleAr() != null && !dto.getTitleAr().trim().isEmpty() ? dto.getTitleAr() : existing.getTitleAr());
                campaignOffer.setDescriptionEn(existing.getDescriptionEn());
                campaignOffer.setDescriptionAr(existing.getDescriptionAr());
                campaignOffer.setTermsEn(existing.getTermsEn());
                campaignOffer.setTermsAr(existing.getTermsAr());
                campaignOffer.setOriginalPrice(originalPrice);
                campaignOffer.setOfferPrice(dto.getNewOfferPrice());
                campaignOffer.setDiscountPct(discountPct);
                campaignOffer.setBadgeType(dto.getBadgeType() != null ? dto.getBadgeType() : existing.getBadgeType());
                campaignOffer.setValidFrom(splitFrom);
                campaignOffer.setValidUntil(splitUntil);
                campaignOffer.setFeatured(existing.isFeatured());
                campaignOffer.setFlash(existing.isFlash());
                campaignOffer.setOnline(existing.isOnline());
                campaignOffer.setInStore(existing.isInStore());
                campaignOffer.setActive(true);
                campaignOffer.setSpecialOffer(specialOffer);
                campaignOffer.setImageUrl(existing.getImageUrl());
                campaignOffer.setThumbnailUrl(existing.getThumbnailUrl());
                campaignOffer.setViewCount(0L);
                campaignOffer.setSaveCount(0);
                campaignOffer.setShareCount(0);

                Offer savedCampaignOffer = offerRepository.save(campaignOffer);

                // Clone image records pointing to same image file URLs
                if (existing.getImages() != null && !existing.getImages().isEmpty()) {
                        for (OfferImage img : existing.getImages()) {
                                OfferImage copyImg = new OfferImage();
                                copyImg.setOffer(savedCampaignOffer);
                                copyImg.setImageUrl(img.getImageUrl());
                                offerImageRepository.save(copyImg);
                                savedCampaignOffer.getImages().add(copyImg);
                        }
                }

                Long afterOfferId = null;

                // Adjust existing offer based on split position
                if (splitFrom.isAfter(origFrom) && splitUntil.isBefore(origUntil)) {
                        // Case A: Split in the middle
                        existing.setValidUntil(splitFrom.minusDays(1));
                        offerRepository.save(existing);

                        Offer afterOffer = new Offer();
                        afterOffer.setStore(existing.getStore());
                        afterOffer.setCity(existing.getCity());
                        afterOffer.setCategory(existing.getCategory());
                        afterOffer.setProduct(existing.getProduct());
                        afterOffer.setTitleEn(existing.getTitleEn());
                        afterOffer.setTitleAr(existing.getTitleAr());
                        afterOffer.setDescriptionEn(existing.getDescriptionEn());
                        afterOffer.setDescriptionAr(existing.getDescriptionAr());
                        afterOffer.setTermsEn(existing.getTermsEn());
                        afterOffer.setTermsAr(existing.getTermsAr());
                        afterOffer.setOriginalPrice(existing.getOriginalPrice());
                        afterOffer.setOfferPrice(existing.getOfferPrice());
                        afterOffer.setDiscountPct(existing.getDiscountPct());
                        afterOffer.setBadgeType(existing.getBadgeType());
                        afterOffer.setValidFrom(splitUntil.plusDays(1));
                        afterOffer.setValidUntil(origUntil);
                        afterOffer.setFeatured(existing.isFeatured());
                        afterOffer.setFlash(existing.isFlash());
                        afterOffer.setOnline(existing.isOnline());
                        afterOffer.setInStore(existing.isInStore());
                        afterOffer.setActive(true);
                        afterOffer.setImageUrl(existing.getImageUrl());
                        afterOffer.setThumbnailUrl(existing.getThumbnailUrl());
                        afterOffer.setViewCount(0L);
                        afterOffer.setSaveCount(0);
                        afterOffer.setShareCount(0);

                        Offer savedAfter = offerRepository.save(afterOffer);
                        afterOfferId = savedAfter.getId();

                        if (existing.getImages() != null && !existing.getImages().isEmpty()) {
                                for (OfferImage img : existing.getImages()) {
                                        OfferImage copyImg = new OfferImage();
                                        copyImg.setOffer(savedAfter);
                                        copyImg.setImageUrl(img.getImageUrl());
                                        offerImageRepository.save(copyImg);
                                        savedAfter.getImages().add(copyImg);
                                }
                        }
                } else if (!splitFrom.isAfter(origFrom) && splitUntil.isBefore(origUntil)) {
                        // Case B: Starts at or before existing start
                        existing.setValidFrom(splitUntil.plusDays(1));
                        offerRepository.save(existing);
                } else if (splitFrom.isAfter(origFrom) && !splitUntil.isBefore(origUntil)) {
                        // Case C: Ends at or after existing end
                        existing.setValidUntil(splitFrom.minusDays(1));
                        offerRepository.save(existing);
                } else {
                        // Case D: Covers entire period or exact replacement
                        existing.setActive(false);
                        offerRepository.save(existing);
                }

                java.util.Map<String, Object> auditDetails = new java.util.HashMap<>();
                auditDetails.put("existingOfferId", existing.getId());
                auditDetails.put("newCampaignOfferId", savedCampaignOffer.getId());
                auditDetails.put("afterOfferId", afterOfferId);
                auditDetails.put("specialOfferId", dto.getSpecialOfferId());
                auditDetails.put("splitFrom", splitFrom.toString());
                auditDetails.put("splitUntil", splitUntil.toString());
                auditDetails.put("newOfferPrice", dto.getNewOfferPrice());

                auditLogService.logAction("OFFER", savedCampaignOffer.getId(), authUser, AuditAction.OFFER_PRICE_PERIOD_SPLIT, auditDetails, request);

                return OfferResponseDto.fromEntity(savedCampaignOffer);
        }

        @Override
        public org.springframework.data.domain.Page<OfferResponseDto> getPagedOffers(
                        CustomUserPrincipal authUser,
                        String search,
                        Integer storeId,
                        String badgeType,
                        String status,
                        Boolean active,
                        int page,
                        int size) {

                if (authUser != null && authUser.getRole() == AdminRole.STORE_MANAGER
                                && authUser.getStoreId() != null) {
                        storeId = authUser.getStoreId();
                }

                org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page,
                                size, org.springframework.data.domain.Sort.by("id").descending());

                com.backend.dealspot.enums.OfferBadgeType badgeEnum = null;
                if (badgeType != null && !badgeType.trim().isEmpty() && !"ALL".equalsIgnoreCase(badgeType)) {
                        try {
                                badgeEnum = com.backend.dealspot.enums.OfferBadgeType
                                                .valueOf(badgeType.trim().toUpperCase());
                        } catch (IllegalArgumentException e) {
                                // invalid badge type enum, ignore
                        }
                }

                String searchQuery = (search != null && !search.trim().isEmpty()) ? search.trim() : null;
                String statusQuery = (status != null && !status.trim().isEmpty() && !"ALL".equalsIgnoreCase(status))
                                ? status.trim().toUpperCase()
                                : null;
                java.time.LocalDate today = java.time.LocalDate.now();

                org.springframework.data.domain.Page<Offer> offersPage = offerRepository.searchOffers(
                                searchQuery,
                                storeId,
                                badgeEnum,
                                statusQuery,
                                today,
                                active,
                                pageable);

                return offersPage.map(OfferResponseDto::fromEntity);
        }
}
