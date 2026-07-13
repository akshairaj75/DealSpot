package com.backend.dealspot.serviceImpl;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.backend.dealspot.dto.flyer.FlyerRequestDto;
import com.backend.dealspot.dto.flyer.FlyerResponseDto;
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

    @Autowired
    FlyerRepository flyerRepository;

    @Autowired
    StoreRepository storeRepository;

    @Autowired
    CityRepository cityRepository;

    @Autowired
    FlyerPageRepository flyerPageRepository;

    @Autowired
    FileStorageService fileStorageService;

    @Transactional
    @Override
    public FlyerResponseDto addFlyer(FlyerRequestDto dto, List<MultipartFile> pages, MultipartFile pdf,
            CustomUserPrincipal authUser, HttpServletRequest request) {

        if (pages == null || pages.isEmpty()) {
            throw new IllegalArgumentException(
                    "At least one flyer page is required");
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
        flyer.setActive(
                dto.getActive() == null || dto.getActive());

        flyer.setViewCount(0L);
        flyer.setTotalPages(pages.size());

        Flyer savedFlyer = flyerRepository.save(flyer);

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
                throw new RuntimeException(
                        "Failed to upload flyer page",
                        e);
            }
        }

        if (pdf != null && !pdf.isEmpty()) {
            try {
                String pdfPath = fileStorageService.storeFile(
                        pdf,
                        "flyers/" + savedFlyer.getId() + "/pdf");

                savedFlyer.setPdfUrl(pdfPath);

            } catch (IOException e) {
                throw new RuntimeException(
                        "Failed to upload flyer PDF",
                        e);
            }
        }

        savedFlyer = flyerRepository.save(savedFlyer);

        return FlyerResponseDto.fromEntity(savedFlyer);

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

}
