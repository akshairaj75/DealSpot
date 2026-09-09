package com.backend.dealspot.serviceImpl;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;


import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.backend.dealspot.dto.flyer.FlyerRequestDto;
import com.backend.dealspot.dto.flyer.FlyerResponseDto;
import com.backend.dealspot.dto.flyer.FlyerResponseDto.FlyerPageResponseDto;
import com.backend.dealspot.entity.City;
import com.backend.dealspot.entity.Flyer;
import com.backend.dealspot.entity.FlyerPage;
import com.backend.dealspot.entity.Store;
import com.backend.dealspot.enums.AdminRole;
import com.backend.dealspot.enums.AuditAction;
import com.backend.dealspot.repository.CityRepository;
import com.backend.dealspot.repository.FlyerPageRepository;
import com.backend.dealspot.repository.FlyerRepository;
import com.backend.dealspot.repository.StoreRepository;
import com.backend.dealspot.security.CustomUserPrincipal;
import com.backend.dealspot.service.AuditLogService;
import com.backend.dealspot.service.FlyerService;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class FlyerServiceImpl implements FlyerService {

        private final FlyerRepository flyerRepository;
        private final StoreRepository storeRepository;
        private final CityRepository cityRepository;
        private final FlyerPageRepository flyerPageRepository;
        private final FileStorageService fileStorageService;
        private final AuditLogService auditLogService;

        public FlyerServiceImpl(FlyerRepository flyerRepository, StoreRepository storeRepository,
                        CityRepository cityRepository, FlyerPageRepository flyerPageRepository,
                        FileStorageService fileStorageService,
                        AuditLogService auditLogService) {
                this.flyerRepository = flyerRepository;
                this.storeRepository = storeRepository;
                this.cityRepository = cityRepository;
                this.flyerPageRepository = flyerPageRepository;
                this.fileStorageService = fileStorageService;
                this.auditLogService = auditLogService;
        }

        @Transactional
        @Override
        public FlyerResponseDto addFlyer(FlyerRequestDto dto, List<MultipartFile> pages, MultipartFile pdf,
                        CustomUserPrincipal authUser, HttpServletRequest request) {

        if (authUser != null && authUser.getRole() == AdminRole.STORE_MANAGER) {
            if (authUser.getStoreId() == null) {
                throw new org.springframework.security.access.AccessDeniedException("No store is assigned to this store manager account");
            }
            if (dto.getStoreId() != null && !dto.getStoreId().equals(authUser.getStoreId())) {
                throw new org.springframework.security.access.AccessDeniedException("Store managers can only create flyers for their own store and branches");
            }
            dto.setStoreId(authUser.getStoreId());
        }

        Store store = storeRepository.findById(dto.getStoreId())
                .orElseThrow(() -> new RuntimeException("Store not found"));


        City city = cityRepository.findById(dto.getCityId())
                .orElseThrow(() -> new RuntimeException("City not found"));

        Flyer flyer = new Flyer();

        flyer.setStore(store);
        flyer.setCity(city);
        flyer.setTitleEn(dto.getTitleEn());
        flyer.setTitleAr(dto.getTitleAr());
        flyer.setDescriptionEn(dto.getDescriptionEn());
        flyer.setDescriptionAr(dto.getDescriptionAr());
        flyer.setValidFrom(dto.getValidFrom());
        flyer.setValidUntil(dto.getValidUntil());
        flyer.setActive(dto.getActive() == null || dto.getActive());
        flyer.setViewCount(0L);
        flyer.setTotalPages(pages != null ? pages.size() : 0);

        Flyer savedFlyer = flyerRepository.save(flyer);

        if (pages != null && !pages.isEmpty()) {
            for (int i = 0; i < pages.size(); i++) {
                try {
                    String imagePath = fileStorageService.storeFile(
                            pages.get(i),
                            "flyers/" + savedFlyer.getId() + "/pages");

                    FlyerPage flyerPage = new FlyerPage();
                    flyerPage.setFlyer(savedFlyer);
                    flyerPage.setPageNumber(i + 1);
                    flyerPage.setImageUrl(imagePath);
                    flyerPage.setThumbUrl(imagePath);

                    flyerPageRepository.save(flyerPage);
                    savedFlyer.getPages().add(flyerPage);

                    // First page becomes the flyer cover
                    if (i == 0) {
                        savedFlyer.setCoverImageUrl(imagePath);
                    }

                } catch (IOException e) {
                    throw new RuntimeException("Failed to upload flyer page", e);
                }
            }
        }

        if (pdf != null && !pdf.isEmpty()) {
            try {
                String pdfPath = fileStorageService.storeFile(
                        pdf,
                        "flyers/" + savedFlyer.getId() + "/pdf");
                savedFlyer.setPdfUrl(pdfPath);
            } catch (IOException e) {
                throw new RuntimeException("Failed to upload flyer PDF", e);
            }
        }

        savedFlyer = flyerRepository.save(savedFlyer);

        java.util.Map<String, Object> auditPayload = new java.util.HashMap<>();
        auditPayload.put("titleEn", savedFlyer.getTitleEn());
        auditPayload.put("titleAr", savedFlyer.getTitleAr());
        if (savedFlyer.getStore() != null) {
            auditPayload.put("storeId", savedFlyer.getStore().getId());
            auditPayload.put("storeNameEn", savedFlyer.getStore().getNameEn());
        }
        auditLogService.logAction("FLYER", savedFlyer.getId().longValue(), authUser, AuditAction.CREATE, auditPayload, request);

        return FlyerResponseDto.fromEntity(savedFlyer);
    }

    @Transactional
    @Override
    public FlyerResponseDto updateFlyer(Integer flyerId, FlyerRequestDto dto, List<MultipartFile> pages, MultipartFile pdf,
                    CustomUserPrincipal authUser, HttpServletRequest request) {

        Flyer flyer = flyerRepository.findById(flyerId)
                .orElseThrow(() -> new RuntimeException("Flyer not found"));

        if (dto.getStoreId() != null) {
            Store store = storeRepository.findById(dto.getStoreId())
                    .orElseThrow(() -> new RuntimeException("Store not found"));
            flyer.setStore(store);
        }

        if (dto.getCityId() != null) {
            City city = cityRepository.findById(dto.getCityId())
                    .orElseThrow(() -> new RuntimeException("City not found"));
            flyer.setCity(city);
        }

        if (dto.getTitleEn() != null) flyer.setTitleEn(dto.getTitleEn());
        if (dto.getTitleAr() != null) flyer.setTitleAr(dto.getTitleAr());
        if (dto.getDescriptionEn() != null) flyer.setDescriptionEn(dto.getDescriptionEn());
        if (dto.getDescriptionAr() != null) flyer.setDescriptionAr(dto.getDescriptionAr());
        if (dto.getValidFrom() != null) flyer.setValidFrom(dto.getValidFrom());
        if (dto.getValidUntil() != null) flyer.setValidUntil(dto.getValidUntil());
        if (dto.getActive() != null) flyer.setActive(dto.getActive());

        // If new pages are provided, replace them
        if (pages != null && !pages.isEmpty()) {
            flyerPageRepository.deleteAll(flyer.getPages());
            flyer.getPages().clear();
            flyer.setTotalPages(pages.size());

            for (int i = 0; i < pages.size(); i++) {
                try {
                    String imagePath = fileStorageService.storeFile(
                            pages.get(i),
                            "flyers/" + flyer.getId() + "/pages");

                    FlyerPage flyerPage = new FlyerPage();
                    flyerPage.setFlyer(flyer);
                    flyerPage.setPageNumber(i + 1);
                    flyerPage.setImageUrl(imagePath);
                    flyerPage.setThumbUrl(imagePath);

                    flyerPageRepository.save(flyerPage);
                    flyer.getPages().add(flyerPage);

                    if (i == 0) {
                        flyer.setCoverImageUrl(imagePath);
                    }
                } catch (IOException e) {
                    throw new RuntimeException("Failed to upload flyer page", e);
                }
            }
        }

        if (pdf != null && !pdf.isEmpty()) {
            try {
                String pdfPath = fileStorageService.storeFile(
                        pdf,
                        "flyers/" + flyer.getId() + "/pdf");
                flyer.setPdfUrl(pdfPath);
            } catch (IOException e) {
                throw new RuntimeException("Failed to upload flyer PDF", e);
            }
        }

        Flyer updatedFlyer = flyerRepository.save(flyer);

        java.util.Map<String, Object> updateAuditPayload = new java.util.HashMap<>();
        updateAuditPayload.put("titleEn", updatedFlyer.getTitleEn());
        updateAuditPayload.put("titleAr", updatedFlyer.getTitleAr());
        if (updatedFlyer.getStore() != null) {
            updateAuditPayload.put("storeId", updatedFlyer.getStore().getId());
        }
        auditLogService.logAction("FLYER", updatedFlyer.getId().longValue(), authUser, AuditAction.UPDATE, updateAuditPayload, request);

        return FlyerResponseDto.fromEntity(updatedFlyer);
    }

    @Override
    public List<FlyerResponseDto> fetchAllFlyers(CustomUserPrincipal authUser, Integer storeId, Boolean includeExpired) {
        boolean shouldIncludeExpired = Boolean.TRUE.equals(includeExpired);
        LocalDate today = LocalDate.now();
        List<Flyer> flyers;

        if (authUser != null && authUser.getRole() == AdminRole.STORE_MANAGER && authUser.getStoreId() != null && shouldIncludeExpired) {
            flyers = flyerRepository.findByStoreId(authUser.getStoreId());
        } else if (storeId != null) {
            flyers = shouldIncludeExpired
                    ? flyerRepository.findByStoreId(storeId)
                    : flyerRepository.findActiveAndValidFlyersByStoreId(storeId, today);
        } else {
            flyers = shouldIncludeExpired
                    ? flyerRepository.findAll()
                    : flyerRepository.findActiveAndValidFlyers(today);
        }
        return flyers.stream().map(FlyerResponseDto::fromEntity).toList();
    }

    @Transactional(readOnly = true)
    @Override
    public FlyerResponseDto fetchFlyerById(Integer flyerId) {
        Flyer flyer = flyerRepository.findById(flyerId)
                .orElseThrow(() -> new RuntimeException("Flyer not found"));
        FlyerResponseDto dto = FlyerResponseDto.fromEntity(flyer);
        List<FlyerPage> pages = flyerPageRepository.findByFlyerIdOrderByPageNumberAsc(flyerId);
        if (pages != null && !pages.isEmpty()) {
            dto.setPages(pages.stream().map(FlyerPageResponseDto::fromEntity).toList());
        }
        return dto;
    }

    @Transactional
    @Override
    public void deleteFlyer(Integer flyerId, CustomUserPrincipal authUser) {
        Flyer flyer = flyerRepository.findById(flyerId)
                .orElseThrow(() -> new RuntimeException("Flyer not found"));

        if (authUser != null && authUser.getRole() == AdminRole.STORE_MANAGER) {
            if (flyer.getStore() == null || !flyer.getStore().getId().equals(authUser.getStoreId())) {
                throw new AccessDeniedException("You are not authorized to delete flyers for another store");
            }
        }

        java.util.Map<String, Object> deleteAuditPayload = new java.util.HashMap<>();
        deleteAuditPayload.put("titleEn", flyer.getTitleEn());
        deleteAuditPayload.put("titleAr", flyer.getTitleAr());
        if (flyer.getStore() != null) {
            deleteAuditPayload.put("storeId", flyer.getStore().getId());
        }

        flyerPageRepository.deleteAll(flyer.getPages());
        flyerRepository.delete(flyer);

        auditLogService.logAction("FLYER", flyerId.longValue(), authUser, AuditAction.DELETE, deleteAuditPayload, null);
    }

    @Override
    public List<FlyerPageResponseDto> fetchPagesByFlyerId(Integer flyerId) {
        List<FlyerPage> pages = flyerPageRepository.findByFlyerIdOrderByPageNumberAsc(flyerId);
        return pages.stream().map(FlyerPageResponseDto::fromEntity).toList();
    }

    @Transactional
    @Override
    public FlyerPageResponseDto addFlyerPage(Integer flyerId, Integer pageNumber, MultipartFile file, CustomUserPrincipal authUser) {
        Flyer flyer = flyerRepository.findById(flyerId)
                .orElseThrow(() -> new RuntimeException("Flyer not found"));

        if (authUser != null && authUser.getRole() == AdminRole.STORE_MANAGER) {
            if (flyer.getStore() == null || !flyer.getStore().getId().equals(authUser.getStoreId())) {
                throw new AccessDeniedException("You are not authorized to manage flyer pages for another store");
            }
        }

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Page image file is required");
        }

        try {
            String imagePath = fileStorageService.storeFile(file, "flyers/" + flyerId + "/pages");

            List<FlyerPage> currentPages = flyerPageRepository.findByFlyerIdOrderByPageNumberAsc(flyerId);
            int actualPageNum = (pageNumber != null && pageNumber > 0)
                    ? pageNumber
                    : (currentPages.size() + 1);

            FlyerPage flyerPage = new FlyerPage();
            flyerPage.setFlyer(flyer);
            flyerPage.setPageNumber(actualPageNum);
            flyerPage.setImageUrl(imagePath);
            flyerPage.setThumbUrl(imagePath);

            FlyerPage savedPage = flyerPageRepository.save(flyerPage);

            if (actualPageNum == 1 || flyer.getCoverImageUrl() == null) {
                flyer.setCoverImageUrl(imagePath);
            }

            flyer.setTotalPages(currentPages.size() + 1);
            flyerRepository.save(flyer);

            return FlyerPageResponseDto.fromEntity(savedPage);
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload flyer page", e);
        }
    }

    @Transactional
    @Override
    public FlyerPageResponseDto updateFlyerPage(Integer pageId, Integer pageNumber, MultipartFile file, CustomUserPrincipal authUser) {
        FlyerPage flyerPage = flyerPageRepository.findById(pageId)
                .orElseThrow(() -> new RuntimeException("Flyer page not found"));

        if (authUser != null && authUser.getRole() == AdminRole.STORE_MANAGER) {
            if (flyerPage.getFlyer().getStore() == null || !flyerPage.getFlyer().getStore().getId().equals(authUser.getStoreId())) {
                throw new AccessDeniedException("You are not authorized to manage flyer pages for another store");
            }
        }

        if (pageNumber != null && pageNumber > 0) {
            flyerPage.setPageNumber(pageNumber);
        }

        if (file != null && !file.isEmpty()) {
            try {
                String imagePath = fileStorageService.storeFile(file, "flyers/" + flyerPage.getFlyer().getId() + "/pages");
                flyerPage.setImageUrl(imagePath);
                flyerPage.setThumbUrl(imagePath);

                if (flyerPage.getPageNumber() == 1) {
                    flyerPage.getFlyer().setCoverImageUrl(imagePath);
                    flyerRepository.save(flyerPage.getFlyer());
                }
            } catch (IOException e) {
                throw new RuntimeException("Failed to upload updated page image", e);
            }
        }

        FlyerPage savedPage = flyerPageRepository.save(flyerPage);
        return FlyerPageResponseDto.fromEntity(savedPage);
    }

    @Transactional
    @Override
    public void deleteFlyerPage(Integer pageId, CustomUserPrincipal authUser) {
        FlyerPage flyerPage = flyerPageRepository.findById(pageId)
                .orElseThrow(() -> new RuntimeException("Flyer page not found"));

        if (authUser != null && authUser.getRole() == AdminRole.STORE_MANAGER) {
            if (flyerPage.getFlyer().getStore() == null || !flyerPage.getFlyer().getStore().getId().equals(authUser.getStoreId())) {
                throw new AccessDeniedException("You are not authorized to delete flyer pages for another store");
            }
        }

        Flyer flyer = flyerPage.getFlyer();
        flyerPageRepository.delete(flyerPage);

        List<FlyerPage> remainingPages = flyerPageRepository.findByFlyerIdOrderByPageNumberAsc(flyer.getId());
        flyer.setTotalPages(remainingPages.size());

        if (remainingPages.isEmpty()) {
            flyer.setCoverImageUrl(null);
        } else {
            flyer.setCoverImageUrl(remainingPages.get(0).getImageUrl());
        }
        flyerRepository.save(flyer);
    }

}

