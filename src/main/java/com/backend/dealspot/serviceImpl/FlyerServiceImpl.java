package com.backend.dealspot.serviceImpl;

import java.io.IOException;
import java.util.List;

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
import com.backend.dealspot.repository.CityRepository;
import com.backend.dealspot.repository.FlyerPageRepository;
import com.backend.dealspot.repository.FlyerRepository;
import com.backend.dealspot.repository.StoreRepository;
import com.backend.dealspot.security.CustomUserPrincipal;
import com.backend.dealspot.service.FlyerService;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class FlyerServiceImpl implements FlyerService {

        private final FlyerRepository flyerRepository;
        private final StoreRepository storeRepository;
        private final CityRepository cityRepository;
        private final FlyerPageRepository flyerPageRepository;
        private final FileStorageService fileStorageService;

        public FlyerServiceImpl(FlyerRepository flyerRepository, StoreRepository storeRepository,
                        CityRepository cityRepository, FlyerPageRepository flyerPageRepository,
                        FileStorageService fileStorageService) {
                this.flyerRepository = flyerRepository;
                this.storeRepository = storeRepository;
                this.cityRepository = cityRepository;
                this.flyerPageRepository = flyerPageRepository;
                this.fileStorageService = fileStorageService;
        }

        @Transactional
        @Override
        public FlyerResponseDto addFlyer(FlyerRequestDto dto, List<MultipartFile> pages, MultipartFile pdf,
                        CustomUserPrincipal authUser, HttpServletRequest request) {

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
        return FlyerResponseDto.fromEntity(updatedFlyer);
    }

    @Override
    public List<FlyerResponseDto> fetchAllFlyers() {
        List<Flyer> flyers = flyerRepository.findAll();
        return flyers.stream().map(FlyerResponseDto::fromEntity).toList();
    }

    @Override
    public FlyerResponseDto fetchFlyerById(Integer flyerId) {
        Flyer flyer = flyerRepository.findById(flyerId)
                .orElseThrow(() -> new RuntimeException("Flyer not found"));
        return FlyerResponseDto.fromEntity(flyer);
    }

    @Transactional
    @Override
    public void deleteFlyer(Integer flyerId) {
        Flyer flyer = flyerRepository.findById(flyerId)
                .orElseThrow(() -> new RuntimeException("Flyer not found"));
        flyerPageRepository.deleteAll(flyer.getPages());
        flyerRepository.delete(flyer);
    }

    @Override
    public List<FlyerPageResponseDto> fetchPagesByFlyerId(Integer flyerId) {
        List<FlyerPage> pages = flyerPageRepository.findByFlyerIdOrderByPageNumberAsc(flyerId);
        return pages.stream().map(FlyerPageResponseDto::fromEntity).toList();
    }

    @Transactional
    @Override
    public FlyerPageResponseDto addFlyerPage(Integer flyerId, Integer pageNumber, MultipartFile file) {
        Flyer flyer = flyerRepository.findById(flyerId)
                .orElseThrow(() -> new RuntimeException("Flyer not found"));

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
    public FlyerPageResponseDto updateFlyerPage(Integer pageId, Integer pageNumber, MultipartFile file) {
        FlyerPage flyerPage = flyerPageRepository.findById(pageId)
                .orElseThrow(() -> new RuntimeException("Flyer page not found"));

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
    public void deleteFlyerPage(Integer pageId) {
        FlyerPage flyerPage = flyerPageRepository.findById(pageId)
                .orElseThrow(() -> new RuntimeException("Flyer page not found"));

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
