package com.backend.dealspot.serviceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backend.dealspot.dto.store.StoreBranchRegisterDto;
import com.backend.dealspot.dto.store.StoreBranchResponseDto;
import com.backend.dealspot.entity.City;
import com.backend.dealspot.entity.Store;
import com.backend.dealspot.entity.StoreBranch;
import com.backend.dealspot.repository.CityRepository;
import com.backend.dealspot.repository.StoreBranchRepository;
import com.backend.dealspot.repository.StoreRepository;
import com.backend.dealspot.security.CustomUserPrincipal;
import com.backend.dealspot.service.StoreBranchService;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class StoreBranchServiceImpl implements StoreBranchService {

    @Autowired
    StoreBranchRepository storeBranchRepository;

    @Autowired
    StoreRepository storeRepository;

    @Autowired
    CityRepository cityRepository;

    @Override
    public List<StoreBranchResponseDto> fetchAllStoreBranches(Integer storeId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new RuntimeException("Store not found"));

        List<StoreBranch> branches = storeBranchRepository.findByStore(store);

        List<StoreBranchResponseDto> res = branches.stream()
                .map(StoreBranchResponseDto::fromEntity).toList();
        return res;
    }

    @Transactional
    @Override
    public StoreBranchResponseDto addBranch(StoreBranchRegisterDto dto,  CustomUserPrincipal authUser) {

        Store store = storeRepository.findById(dto.getStoreId())
                .orElseThrow(() -> new RuntimeException("Store not found"));

        City city = cityRepository.findById(dto.getCityId())
                .orElseThrow(() -> new RuntimeException("City not found"));

        StoreBranch branch = new StoreBranch();

        branch.setCity(city);
        branch.setStore(store);
        branch.setBranchName(dto.getBranchName());
        branch.setAddressAr(dto.getAddressAr());
        branch.setAddressEn(dto.getAddressEn());
        branch.setLatitude(dto.getLatitude());
        branch.setLongitude(dto.getLongitude());
        branch.setPhone(dto.getPhone());
        branch.setOpenTime(dto.getOpenTime());
        branch.setCloseTime(dto.getCloseTime());
        branch.setTwentyFourHours(dto.getTwentyFourHours());
        branch.setActive(dto.getActive());

        StoreBranch savedBranch = storeBranchRepository.save(branch);

        return StoreBranchResponseDto.fromEntity(savedBranch);
    }


    @Transactional
    @Override
    public StoreBranchResponseDto updateBranch(Integer branchId, StoreBranchRegisterDto dto, CustomUserPrincipal authUser) {
        StoreBranch branch = storeBranchRepository.findById(branchId)
                .orElseThrow(() -> new RuntimeException("Branch not found"));

        City city = cityRepository.findById(dto.getCityId())
                .orElseThrow(() -> new RuntimeException("City not found"));

        branch.setCity(city);
        branch.setBranchName(dto.getBranchName());
        branch.setAddressAr(dto.getAddressAr());
        branch.setAddressEn(dto.getAddressEn());
        branch.setLatitude(dto.getLatitude());
        branch.setLongitude(dto.getLongitude());
        branch.setPhone(dto.getPhone());
        branch.setOpenTime(dto.getOpenTime());
        branch.setCloseTime(dto.getCloseTime());
        branch.setTwentyFourHours(dto.getTwentyFourHours());
        branch.setActive(dto.getActive());

        StoreBranch savedBranch = storeBranchRepository.save(branch);

        return StoreBranchResponseDto.fromEntity(savedBranch);
    }

    @Override
    public void deleteBranch(Integer branchId, CustomUserPrincipal authUser, HttpServletRequest request) {
        StoreBranch branch = storeBranchRepository.findById(branchId)
                .orElseThrow(() -> new RuntimeException("Branch not found"));

        storeBranchRepository.delete(branch);
    }

}
