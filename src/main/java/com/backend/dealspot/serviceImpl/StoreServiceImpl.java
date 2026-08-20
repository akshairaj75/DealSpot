package com.backend.dealspot.serviceImpl;

import java.io.IOException;
import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.backend.dealspot.dto.store.StoreRegisterDto;
import com.backend.dealspot.dto.store.StoreResponseDto;
import com.backend.dealspot.entity.AdminUser;
import com.backend.dealspot.entity.Category;
import com.backend.dealspot.entity.City;
import com.backend.dealspot.entity.Store;
import com.backend.dealspot.enums.AdminRole;
import com.backend.dealspot.repository.AdminUserRepository;
import com.backend.dealspot.repository.CategoryRepository;
import com.backend.dealspot.repository.CityRepository;
import com.backend.dealspot.repository.StoreRepository;
import com.backend.dealspot.security.CustomUserPrincipal;
import com.backend.dealspot.service.StoreService;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class StoreServiceImpl implements StoreService {

    private final StoreRepository storeRepository;
    private final CityRepository cityRepository;
    private final CategoryRepository categoryRepository;
    private final FileStorageService fileStorageService;
    private final AdminUserRepository adminUserRepository;
    private final PasswordEncoder passwordEncoder;

    public StoreServiceImpl(StoreRepository storeRepository, CityRepository cityRepository,
            CategoryRepository categoryRepository,
            FileStorageService fileStorageService,
            AdminUserRepository adminUserRepository,
            PasswordEncoder passwordEncoder) {
        this.storeRepository = storeRepository;
        this.cityRepository = cityRepository;
        this.categoryRepository = categoryRepository;
        this.fileStorageService = fileStorageService;
        this.adminUserRepository = adminUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    @Override
    public StoreResponseDto createStore(StoreRegisterDto dto, MultipartFile file, CustomUserPrincipal authUser,
            HttpServletRequest request) {

        City city = cityRepository.findById(dto.getCityId())
                .orElseThrow(() -> new RuntimeException("City not found"));

        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        Store newStore = new Store();
        newStore.setNameEn(dto.getNameEn());
        newStore.setNameAr(dto.getNameAr());
        newStore.setDescriptionEn(dto.getDescriptionEn());
        newStore.setDescriptionAr(dto.getDescriptionAr());
        newStore.setBannerUrl(dto.getBannerUrl());
        newStore.setWebsite(dto.getWebsite());
        newStore.setContactPhone(dto.getContactPhone());
        newStore.setContactEmail(dto.getContactEmail());
        newStore.setVatNumber(dto.getVatNumber());
        newStore.setCrNumber(dto.getCrNumber());
        newStore.setCity(city);
        newStore.setCategory(category);
        if (file != null && !file.isEmpty()) {
            try {
                String imageFile = fileStorageService.storeFile(file, "stores/logo");
                newStore.setLogoUrl(imageFile);
            } catch (IOException e) {
                throw new RuntimeException("Failed to upload store logo", e);
            }
        }

        Store saved = storeRepository.save(newStore);

        // Auto-provision or link Store Manager account if managerEmail is provided
        String managerEmail = (dto.getManagerEmail() != null && !dto.getManagerEmail().trim().isEmpty())
                ? dto.getManagerEmail().trim()
                : (dto.getContactEmail() != null && !dto.getContactEmail().trim().isEmpty() ? dto.getContactEmail().trim() : null);

        if (managerEmail != null && !managerEmail.isEmpty()) {
            final String finalManagerEmail = managerEmail;
            AdminUser manager = adminUserRepository.findByEmail(finalManagerEmail)
                    .orElseGet(() -> {
                        AdminUser newAdmin = new AdminUser();
                        String name = (dto.getManagerName() != null && !dto.getManagerName().trim().isEmpty())
                                ? dto.getManagerName().trim()
                                : dto.getNameEn();
                        newAdmin.setFullName(name);
                        newAdmin.setEmail(finalManagerEmail);
                        String rawPass = (dto.getManagerPassword() != null && !dto.getManagerPassword().trim().isEmpty())
                                ? dto.getManagerPassword().trim()
                                : "Partner@123";
                        newAdmin.setPasswordHash(passwordEncoder.encode(rawPass));
                        return newAdmin;
                    });


            manager.setRole(AdminRole.STORE_MANAGER);
            manager.setStore(saved);
            manager.setActive(true);
            adminUserRepository.save(manager);
        }

        return StoreResponseDto.fromEntity(saved);
    }


    @Override
    public List<StoreResponseDto> fetchAllStores(CustomUserPrincipal authUser, HttpServletRequest request) {

        List<Store> stores = storeRepository.findAll();

        return stores.stream().map(StoreResponseDto::fromEntity).toList();
    }

    @Override
    public StoreResponseDto fetchStore(Integer storeId, CustomUserPrincipal authUser, HttpServletRequest request) {

        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new RuntimeException("Store not found"));

        return StoreResponseDto.fromEntity(store);
    }

    @Transactional
    @Override
    public StoreResponseDto updateStore(
            Integer storeId,
            StoreRegisterDto dto,
            MultipartFile file,
            CustomUserPrincipal authUser,
            HttpServletRequest request) {

        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new RuntimeException("Store not found"));

        if (dto.getNameEn() != null && !dto.getNameEn().isEmpty()) {
            store.setNameEn(dto.getNameEn());
        }
        if (dto.getNameAr() != null && !dto.getNameAr().isEmpty()) {
            store.setNameAr(dto.getNameAr());
        }
        if (dto.getDescriptionEn() != null && !dto.getDescriptionEn().isEmpty()) {
            store.setDescriptionEn(dto.getDescriptionEn());
        }
        if (dto.getDescriptionAr() != null && !dto.getDescriptionAr().isEmpty()) {
            store.setDescriptionAr(dto.getDescriptionAr());
        }
        // if (dto.getLogoUrl() != null && !dto.getLogoUrl().isEmpty()) {
        // store.setLogoUrl(dto.getLogoUrl());
        // }
        if (dto.getBannerUrl() != null && !dto.getBannerUrl().isEmpty()) {
            store.setBannerUrl(dto.getBannerUrl());
        }
        if (dto.getWebsite() != null && !dto.getWebsite().isEmpty()) {
            store.setWebsite(dto.getWebsite());
        }
        if (dto.getContactPhone() != null && !dto.getContactPhone().isEmpty()) {
            store.setContactPhone(dto.getContactPhone());
        }
        if (dto.getContactEmail() != null && !dto.getContactEmail().isEmpty()) {
            store.setContactEmail(dto.getContactEmail());
        }
        if (dto.getVatNumber() != null && !dto.getVatNumber().isEmpty()) {
            store.setVatNumber(dto.getVatNumber());
        }
        if (dto.getCrNumber() != null && !dto.getCrNumber().isEmpty()) {
            store.setCrNumber(dto.getCrNumber());
        }
        if (dto.getCityId() != null) {
            City city = cityRepository.findById(dto.getCityId())
                    .orElseThrow(() -> new RuntimeException("City not found"));
            store.setCity(city);
        }
        if (dto.getCategoryId() != null) {
            Category category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));
            store.setCategory(category);
        }

        if (file != null && !file.isEmpty()) {
            try {
                String logoUrl = fileStorageService.storeFile(
                        file,
                        "stores/logo");

                store.setLogoUrl(logoUrl);

            } catch (IOException e) {
                throw new RuntimeException(
                        "Failed to upload store logo",
                        e);
            }
        }
        Store saved = storeRepository.save(store);
        return StoreResponseDto.fromEntity(saved);
    }

    @Transactional
    @Override
    public void deleteStore(Integer storeId, CustomUserPrincipal authUser, HttpServletRequest request) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new RuntimeException("Store not found"));
        storeRepository.delete(store);
    }

}
