package com.backend.dealspot.service;

import java.util.List;

import com.backend.dealspot.dto.store.StoreBranchRegisterDto;
import com.backend.dealspot.dto.store.StoreBranchResponseDto;
import com.backend.dealspot.security.CustomUserPrincipal;

import jakarta.servlet.http.HttpServletRequest;
public interface StoreBranchService {


    List<StoreBranchResponseDto> fetchAllStoreBranches(Integer storeId);

    StoreBranchResponseDto addBranch(StoreBranchRegisterDto dto, CustomUserPrincipal authUser);

    StoreBranchResponseDto updateBranch(Integer branchId, StoreBranchRegisterDto dto, CustomUserPrincipal authUser);

    void deleteBranch(Integer branchId, CustomUserPrincipal authUser, HttpServletRequest request);


}
