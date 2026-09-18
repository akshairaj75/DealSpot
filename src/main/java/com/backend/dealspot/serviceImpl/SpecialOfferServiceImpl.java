package com.backend.dealspot.serviceImpl;

import com.backend.dealspot.dto.offer.OfferPeriodSplitRequestDto;
import com.backend.dealspot.dto.offer.OfferResponseDto;
import com.backend.dealspot.dto.specialoffer.SpecialOfferRequestDto;
import com.backend.dealspot.dto.specialoffer.SpecialOfferResponseDto;
import com.backend.dealspot.entity.City;
import com.backend.dealspot.entity.Offer;
import com.backend.dealspot.entity.SpecialOffer;
import com.backend.dealspot.entity.Store;
import com.backend.dealspot.enums.AdminRole;
import com.backend.dealspot.enums.AuditAction;
import com.backend.dealspot.repository.CityRepository;
import com.backend.dealspot.repository.OfferRepository;
import com.backend.dealspot.repository.SpecialOfferRepository;
import com.backend.dealspot.repository.StoreRepository;
import com.backend.dealspot.security.CustomUserPrincipal;
import com.backend.dealspot.service.AuditLogService;
import com.backend.dealspot.service.OfferService;
import com.backend.dealspot.service.SpecialOfferService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SpecialOfferServiceImpl implements SpecialOfferService {

    private final SpecialOfferRepository specialOfferRepository;
    private final OfferRepository offerRepository;
    private final StoreRepository storeRepository;
    private final CityRepository cityRepository;
    private final FileStorageService fileStorageService;
    private final AuditLogService auditLogService;
    private final OfferService offerService;

    public SpecialOfferServiceImpl(
            SpecialOfferRepository specialOfferRepository,
            OfferRepository offerRepository,
            StoreRepository storeRepository,
            CityRepository cityRepository,
            FileStorageService fileStorageService,
            AuditLogService auditLogService,
            OfferService offerService) {
        this.specialOfferRepository = specialOfferRepository;
        this.offerRepository = offerRepository;
        this.storeRepository = storeRepository;
        this.cityRepository = cityRepository;
        this.fileStorageService = fileStorageService;
        this.auditLogService = auditLogService;
        this.offerService = offerService;
    }

    @Transactional
    @Override
    public SpecialOfferResponseDto addSpecialOffer(
            SpecialOfferRequestDto dto,
            MultipartFile bannerFile,
            CustomUserPrincipal authUser,
            HttpServletRequest request) {

        if (authUser != null && authUser.getRole() == AdminRole.STORE_MANAGER) {
            if (authUser.getStoreId() == null) {
                throw new AccessDeniedException("No store is assigned to this store manager account");
            }
            if (dto.getStoreId() != null && !dto.getStoreId().equals(authUser.getStoreId().longValue())) {
                throw new AccessDeniedException("Store managers can only create special offers for their own store");
            }
            dto.setStoreId(authUser.getStoreId().longValue());
        }

        SpecialOffer specialOffer = new SpecialOffer();
        specialOffer.setTitleEn(dto.getTitleEn());
        specialOffer.setTitleAr(dto.getTitleAr());
        specialOffer.setDescriptionEn(dto.getDescriptionEn());
        specialOffer.setDescriptionAr(dto.getDescriptionAr());
        specialOffer.setBadgeText(dto.getBadgeText());
        specialOffer.setValidFrom(dto.getValidFrom());
        specialOffer.setValidUntil(dto.getValidUntil());
        specialOffer.setFeatured(dto.getFeatured() != null ? dto.getFeatured() : true);
        specialOffer.setActive(dto.getActive() != null ? dto.getActive() : true);
        specialOffer.setViewCount(0L);

        if (dto.getStoreId() != null) {
            Store store = storeRepository.findById(dto.getStoreId().intValue()).orElse(null);
            specialOffer.setStore(store);
        }

        if (dto.getCityId() != null) {
            City city = cityRepository.findById(dto.getCityId().intValue()).orElse(null);
            specialOffer.setCity(city);
        }

        if (bannerFile != null && !bannerFile.isEmpty()) {
            try {
                String bannerPath = fileStorageService.storeFile(bannerFile, "special-offers");
                specialOffer.setBannerUrl(bannerPath);
                specialOffer.setThumbnailUrl(bannerPath);
            } catch (IOException e) {
                throw new RuntimeException("Failed to upload campaign banner image", e);
            }
        }

        SpecialOffer saved = specialOfferRepository.save(specialOffer);

        // Attach initial offers if provided
        if (dto.getOfferIds() != null && !dto.getOfferIds().isEmpty()) {
            for (Long offerId : dto.getOfferIds()) {
                offerRepository.findById(offerId).ifPresent(offer -> {
                    // Check store consistency if special offer is store-specific
                    if (saved.getStore() == null || (offer.getStore() != null && offer.getStore().getId().equals(saved.getStore().getId()))) {
                        offer.setSpecialOffer(saved);
                        offerRepository.save(offer);
                    }
                });
            }
        }

        Map<String, Object> auditPayload = new HashMap<>();
        auditPayload.put("titleEn", saved.getTitleEn());
        auditPayload.put("titleAr", saved.getTitleAr());
        if (saved.getStore() != null) {
            auditPayload.put("storeId", saved.getStore().getId());
        }
        auditLogService.logAction("SPECIAL_OFFER", saved.getId(), authUser, AuditAction.CREATE_SPECIAL_OFFER, auditPayload, request);

        // Re-fetch to load attached offers
        SpecialOffer refreshed = specialOfferRepository.findById(saved.getId()).orElse(saved);
        return SpecialOfferResponseDto.fromEntity(refreshed, true);
    }

    @Transactional
    @Override
    public SpecialOfferResponseDto updateSpecialOffer(
            Long id,
            SpecialOfferRequestDto dto,
            MultipartFile bannerFile,
            CustomUserPrincipal authUser,
            HttpServletRequest request) {

        SpecialOffer specialOffer = specialOfferRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Special offer not found with id: " + id));

        if (authUser != null && authUser.getRole() == AdminRole.STORE_MANAGER) {
            if (authUser.getStoreId() == null || specialOffer.getStore() == null || !specialOffer.getStore().getId().equals(authUser.getStoreId())) {
                throw new AccessDeniedException("You are not authorized to update special offers for another store");
            }
        }

        specialOffer.setTitleEn(dto.getTitleEn());
        specialOffer.setTitleAr(dto.getTitleAr());
        specialOffer.setDescriptionEn(dto.getDescriptionEn());
        specialOffer.setDescriptionAr(dto.getDescriptionAr());
        specialOffer.setBadgeText(dto.getBadgeText());
        specialOffer.setValidFrom(dto.getValidFrom());
        specialOffer.setValidUntil(dto.getValidUntil());
        if (dto.getFeatured() != null) {
            specialOffer.setFeatured(dto.getFeatured());
        }
        if (dto.getActive() != null) {
            specialOffer.setActive(dto.getActive());
        }

        if (dto.getStoreId() != null) {
            storeRepository.findById(dto.getStoreId().intValue()).ifPresent(specialOffer::setStore);
        } else if (authUser == null || authUser.getRole() != AdminRole.STORE_MANAGER) {
            specialOffer.setStore(null);
        }

        if (dto.getCityId() != null) {
            cityRepository.findById(dto.getCityId().intValue()).ifPresent(specialOffer::setCity);
        } else {
            specialOffer.setCity(null);
        }

        if (bannerFile != null && !bannerFile.isEmpty()) {
            try {
                if (specialOffer.getBannerUrl() != null) {
                    fileStorageService.deleteFile(specialOffer.getBannerUrl(), "special-offers");
                }
                String bannerPath = fileStorageService.storeFile(bannerFile, "special-offers");
                specialOffer.setBannerUrl(bannerPath);
                specialOffer.setThumbnailUrl(bannerPath);
            } catch (IOException e) {
                throw new RuntimeException("Failed to upload campaign banner image", e);
            }
        }

        SpecialOffer saved = specialOfferRepository.save(specialOffer);

        // Synchronize offers if offerIds is provided
        if (dto.getOfferIds() != null) {
            List<Offer> currentOffers = offerRepository.findBySpecialOfferId(saved.getId());
            // Detach offers not in the new list
            for (Offer o : currentOffers) {
                if (!dto.getOfferIds().contains(o.getId())) {
                    o.setSpecialOffer(null);
                    offerRepository.save(o);
                }
            }
            // Attach new offers
            for (Long oId : dto.getOfferIds()) {
                offerRepository.findById(oId).ifPresent(o -> {
                    if (o.getSpecialOffer() == null || !o.getSpecialOffer().getId().equals(saved.getId())) {
                        o.setSpecialOffer(saved);
                        offerRepository.save(o);
                    }
                });
            }
        }

        Map<String, Object> auditPayload = new HashMap<>();
        auditPayload.put("titleEn", saved.getTitleEn());
        auditPayload.put("titleAr", saved.getTitleAr());
        auditLogService.logAction("SPECIAL_OFFER", saved.getId(), authUser, AuditAction.UPDATE_SPECIAL_OFFER, auditPayload, request);

        SpecialOffer refreshed = specialOfferRepository.findById(saved.getId()).orElse(saved);
        return SpecialOfferResponseDto.fromEntity(refreshed, true);
    }

    @Transactional
    @Override
    public void deleteSpecialOffer(Long id, CustomUserPrincipal authUser, HttpServletRequest request) {
        SpecialOffer specialOffer = specialOfferRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Special offer not found with id: " + id));

        if (authUser != null && authUser.getRole() == AdminRole.STORE_MANAGER) {
            if (authUser.getStoreId() == null || specialOffer.getStore() == null || !specialOffer.getStore().getId().equals(authUser.getStoreId())) {
                throw new AccessDeniedException("You are not authorized to delete special offers for another store");
            }
        }

        // Detach all offers linked to this campaign
        List<Offer> attachedOffers = offerRepository.findBySpecialOfferId(id);
        for (Offer o : attachedOffers) {
            o.setSpecialOffer(null);
            offerRepository.save(o);
        }

        if (specialOffer.getBannerUrl() != null) {
            fileStorageService.deleteFile(specialOffer.getBannerUrl(), "special-offers");
        }

        Map<String, Object> auditPayload = new HashMap<>();
        auditPayload.put("titleEn", specialOffer.getTitleEn());
        auditPayload.put("titleAr", specialOffer.getTitleAr());

        specialOfferRepository.delete(specialOffer);
        auditLogService.logAction("SPECIAL_OFFER", id, authUser, AuditAction.DELETE_SPECIAL_OFFER, auditPayload, request);
    }

    @Transactional
    @Override
    public SpecialOfferResponseDto getSpecialOfferById(Long id) {
        SpecialOffer specialOffer = specialOfferRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Special offer not found with id: " + id));

        // Increment view count
        specialOffer.setViewCount(specialOffer.getViewCount() != null ? specialOffer.getViewCount() + 1 : 1L);
        specialOfferRepository.save(specialOffer);

        return SpecialOfferResponseDto.fromEntity(specialOffer, true);
    }

    @Override
    public List<SpecialOfferResponseDto> fetchAllActiveSpecialOffers(Integer storeId, Integer cityId) {
        LocalDate today = LocalDate.now();
        List<SpecialOffer> list = specialOfferRepository.findActiveSpecialOffers(today, storeId, cityId);
        return list.stream()
                .map(so -> SpecialOfferResponseDto.fromEntity(so, true))
                .collect(Collectors.toList());
    }

    @Override
    public Page<SpecialOfferResponseDto> getPagedSpecialOffers(
            CustomUserPrincipal authUser,
            String search,
            Integer storeId,
            String status,
            Boolean active,
            int page,
            int size) {

        Integer effectiveStoreId = storeId;
        if (authUser != null && authUser.getRole() == AdminRole.STORE_MANAGER) {
            effectiveStoreId = authUser.getStoreId();
        }

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<SpecialOffer> resultPage = specialOfferRepository.searchSpecialOffers(
                search, effectiveStoreId, status, LocalDate.now(), active, pageRequest);

        return resultPage.map(so -> SpecialOfferResponseDto.fromEntity(so, true));
    }

    @Transactional
    @Override
    public SpecialOfferResponseDto attachDeals(
            Long specialOfferId,
            List<Long> offerIds,
            CustomUserPrincipal authUser,
            HttpServletRequest request) {

        SpecialOffer specialOffer = specialOfferRepository.findById(specialOfferId)
                .orElseThrow(() -> new RuntimeException("Special offer not found with id: " + specialOfferId));

        if (authUser != null && authUser.getRole() == AdminRole.STORE_MANAGER) {
            if (authUser.getStoreId() == null || specialOffer.getStore() == null || !specialOffer.getStore().getId().equals(authUser.getStoreId())) {
                throw new AccessDeniedException("Unauthorized");
            }
        }

        if (offerIds != null) {
            for (Long oId : offerIds) {
                offerRepository.findById(oId).ifPresent(offer -> {
                    // Prevent attaching offers from another store if campaign is store-specific
                    if (specialOffer.getStore() == null || (offer.getStore() != null && offer.getStore().getId().equals(specialOffer.getStore().getId()))) {
                        offer.setSpecialOffer(specialOffer);
                        offerRepository.save(offer);
                    }
                });
            }
        }

        Map<String, Object> auditPayload = new HashMap<>();
        auditPayload.put("attachedOffersCount", offerIds != null ? offerIds.size() : 0);
        auditLogService.logAction("SPECIAL_OFFER", specialOfferId, authUser, AuditAction.ATTACH_DEALS_SPECIAL_OFFER, auditPayload, request);

        SpecialOffer refreshed = specialOfferRepository.findById(specialOfferId).orElse(specialOffer);
        return SpecialOfferResponseDto.fromEntity(refreshed, true);
    }

    @Transactional
    @Override
    public SpecialOfferResponseDto removeDeal(
            Long specialOfferId,
            Long offerId,
            CustomUserPrincipal authUser,
            HttpServletRequest request) {

        SpecialOffer specialOffer = specialOfferRepository.findById(specialOfferId)
                .orElseThrow(() -> new RuntimeException("Special offer not found with id: " + specialOfferId));

        if (authUser != null && authUser.getRole() == AdminRole.STORE_MANAGER) {
            if (authUser.getStoreId() == null || specialOffer.getStore() == null || !specialOffer.getStore().getId().equals(authUser.getStoreId())) {
                throw new AccessDeniedException("Unauthorized");
            }
        }

        offerRepository.findById(offerId).ifPresent(offer -> {
            if (offer.getSpecialOffer() != null && offer.getSpecialOffer().getId().equals(specialOfferId)) {
                offer.setSpecialOffer(null);
                offerRepository.save(offer);
            }
        });

        SpecialOffer refreshed = specialOfferRepository.findById(specialOfferId).orElse(specialOffer);
        return SpecialOfferResponseDto.fromEntity(refreshed, true);
    }

    @Transactional
    @Override
    public OfferResponseDto splitAndCreateCampaignOffer(
            Long specialOfferId,
            OfferPeriodSplitRequestDto dto,
            CustomUserPrincipal authUser,
            HttpServletRequest request) {

        SpecialOffer specialOffer = specialOfferRepository.findById(specialOfferId)
                .orElseThrow(() -> new RuntimeException("Special offer campaign not found with id: " + specialOfferId));

        if (authUser != null && authUser.getRole() == AdminRole.STORE_MANAGER) {
            if (authUser.getStoreId() == null || specialOffer.getStore() == null || !specialOffer.getStore().getId().equals(authUser.getStoreId())) {
                throw new AccessDeniedException("Unauthorized to manage offers for this campaign");
            }
        }

        dto.setSpecialOfferId(specialOfferId);
        return offerService.splitAndCreateOffer(dto, authUser, request);
    }
}
