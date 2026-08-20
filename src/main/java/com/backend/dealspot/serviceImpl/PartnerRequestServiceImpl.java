package com.backend.dealspot.serviceImpl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backend.dealspot.dto.partner.PartnerApplyRequestDto;
import com.backend.dealspot.dto.partner.PartnerRequestResponseDto;
import com.backend.dealspot.entity.AdminUser;
import com.backend.dealspot.entity.Category;
import com.backend.dealspot.entity.City;
import com.backend.dealspot.entity.PartnerRequest;
import com.backend.dealspot.entity.Store;
import com.backend.dealspot.enums.AdminRole;
import com.backend.dealspot.enums.PartnerRequestStatus;
import com.backend.dealspot.repository.AdminUserRepository;
import com.backend.dealspot.repository.CategoryRepository;
import com.backend.dealspot.repository.CityRepository;
import com.backend.dealspot.repository.PartnerRequestRepository;
import com.backend.dealspot.repository.StoreRepository;
import com.backend.dealspot.service.PartnerRequestService;

@Service
public class PartnerRequestServiceImpl implements PartnerRequestService {

    private final PartnerRequestRepository partnerRequestRepository;
    private final StoreRepository storeRepository;
    private final AdminUserRepository adminUserRepository;
    private final CityRepository cityRepository;
    private final CategoryRepository categoryRepository;
    private final PasswordEncoder passwordEncoder;

    public PartnerRequestServiceImpl(
            PartnerRequestRepository partnerRequestRepository,
            StoreRepository storeRepository,
            AdminUserRepository adminUserRepository,
            CityRepository cityRepository,
            CategoryRepository categoryRepository,
            PasswordEncoder passwordEncoder) {
        this.partnerRequestRepository = partnerRequestRepository;
        this.storeRepository = storeRepository;
        this.adminUserRepository = adminUserRepository;
        this.cityRepository = cityRepository;
        this.categoryRepository = categoryRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public PartnerRequestResponseDto submitApplication(PartnerApplyRequestDto dto) {
        if (dto.getApplicantEmail() == null || dto.getApplicantEmail().isBlank()) {
            throw new IllegalArgumentException("Applicant email is required");
        }

        City city = cityRepository.findById(dto.getCityId())
                .orElseThrow(() -> new IllegalArgumentException("Selected city does not exist"));

        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Selected category does not exist"));

        PartnerRequest req = new PartnerRequest();
        req.setApplicantName(dto.getApplicantName());
        req.setApplicantEmail(dto.getApplicantEmail());
        req.setApplicantPhone(dto.getApplicantPhone());
        req.setStoreNameEn(dto.getStoreNameEn());
        req.setStoreNameAr(dto.getStoreNameAr());
        req.setDescriptionEn(dto.getDescriptionEn());
        req.setDescriptionAr(dto.getDescriptionAr());
        req.setCity(city);
        req.setCategory(category);
        req.setCrNumber(dto.getCrNumber());
        req.setVatNumber(dto.getVatNumber());
        req.setWebsite(dto.getWebsite());
        req.setLogoUrl(dto.getLogoUrl());
        req.setBannerUrl(dto.getBannerUrl());
        req.setContactAddress(dto.getContactAddress());
        req.setStatus(PartnerRequestStatus.PENDING);

        PartnerRequest saved = partnerRequestRepository.save(req);
        return PartnerRequestResponseDto.fromEntity(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PartnerRequestResponseDto> getAllRequests(PartnerRequestStatus status) {
        if (status != null) {
            return partnerRequestRepository.findByStatusOrderByCreatedAtDesc(status)
                    .stream()
                    .map(PartnerRequestResponseDto::fromEntity)
                    .toList();
        }
        return partnerRequestRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(PartnerRequestResponseDto::fromEntity)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PartnerRequestResponseDto getRequestById(Long id) {
        PartnerRequest req = partnerRequestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Partner request not found with id: " + id));
        return PartnerRequestResponseDto.fromEntity(req);
    }

    @Override
    @Transactional
    public PartnerRequestResponseDto approveRequest(Long id) {
        PartnerRequest req = partnerRequestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Partner request not found with id: " + id));

        if (req.getStatus() == PartnerRequestStatus.APPROVED) {
            throw new IllegalStateException("This request is already approved");
        }

        // 1. Create and persist new Store
        Store store = new Store();
        store.setNameEn(req.getStoreNameEn());
        store.setNameAr(req.getStoreNameAr());
        store.setDescriptionEn(req.getDescriptionEn());
        store.setDescriptionAr(req.getDescriptionAr());
        store.setCity(req.getCity());
        store.setCategory(req.getCategory());
        store.setCrNumber(req.getCrNumber());
        store.setVatNumber(req.getVatNumber());
        store.setWebsite(req.getWebsite());
        store.setLogoUrl(req.getLogoUrl());
        store.setBannerUrl(req.getBannerUrl());
        store.setContactPhone(req.getApplicantPhone());
        store.setContactEmail(req.getApplicantEmail());
        store.setVerified(true);
        store.setActive(true);

        Store savedStore = storeRepository.save(store);

        // 2. Create or bind AdminUser with role STORE_MANAGER
        AdminUser admin = adminUserRepository.findByEmail(req.getApplicantEmail())
                .orElseGet(() -> {
                    AdminUser newAdmin = new AdminUser();
                    newAdmin.setFullName(req.getApplicantName());
                    newAdmin.setEmail(req.getApplicantEmail());
                    newAdmin.setPasswordHash(passwordEncoder.encode("Partner@123"));
                    return newAdmin;
                });

        admin.setRole(AdminRole.STORE_MANAGER);
        admin.setStore(savedStore);
        admin.setActive(true);
        adminUserRepository.save(admin);

        // 3. Mark request as approved
        req.setStatus(PartnerRequestStatus.APPROVED);
        req.setCreatedStore(savedStore);
        req.setReviewedAt(LocalDateTime.now());
        PartnerRequest updatedReq = partnerRequestRepository.save(req);

        return PartnerRequestResponseDto.fromEntity(updatedReq);
    }

    @Override
    @Transactional
    public PartnerRequestResponseDto rejectRequest(Long id, String reason) {
        PartnerRequest req = partnerRequestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Partner request not found with id: " + id));

        req.setStatus(PartnerRequestStatus.REJECTED);
        req.setRejectionReason(reason);
        req.setReviewedAt(LocalDateTime.now());
        PartnerRequest updatedReq = partnerRequestRepository.save(req);

        return PartnerRequestResponseDto.fromEntity(updatedReq);
    }
}
