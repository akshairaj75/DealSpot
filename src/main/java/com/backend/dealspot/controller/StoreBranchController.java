package com.backend.dealspot.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.dealspot.dto.store.StoreBranchRegisterDto;
import com.backend.dealspot.dto.store.StoreBranchResponseDto;
import com.backend.dealspot.security.CustomUserPrincipal;
import com.backend.dealspot.service.StoreBranchService;

import jakarta.servlet.http.HttpServletRequest;


@RestController
@RequestMapping("/api/dealspot/store-branches")
public class StoreBranchController {

    private final StoreBranchService storeBranchService;

    public StoreBranchController(StoreBranchService storeBranchService) {
        this.storeBranchService = storeBranchService;
    }

    @GetMapping("/store/{storeId}/branches")
    public ResponseEntity<List<StoreBranchResponseDto>> fetchAllStoreBranches(
            @PathVariable("storeId") Integer storeId) {
        List<StoreBranchResponseDto> res = storeBranchService.fetchAllStoreBranches(storeId);
        return ResponseEntity.ok(res);
    }

    @PostMapping("/store/add-branch")
    public ResponseEntity<StoreBranchResponseDto> addBranch(
            @RequestBody StoreBranchRegisterDto dto,
            @AuthenticationPrincipal CustomUserPrincipal authUser) {
        StoreBranchResponseDto res = storeBranchService.addBranch(dto, authUser);
        return ResponseEntity.ok(res);
    }

    @PostMapping("/create/bulk")
    public ResponseEntity<List<StoreBranchResponseDto>> addBranchBulk(
            @RequestBody List<StoreBranchRegisterDto> dtos,
            @AuthenticationPrincipal CustomUserPrincipal authUser) {
        List<StoreBranchResponseDto> res = dtos.stream()
                .map(dto -> storeBranchService.addBranch(dto, authUser))
                .toList();
        return ResponseEntity.ok(res);
    }

    @PutMapping("/update/{branchId}")
    public ResponseEntity<StoreBranchResponseDto> updateBranch(
            @PathVariable("branchId") Integer branchId,
            @RequestBody StoreBranchRegisterDto dto,
            @AuthenticationPrincipal CustomUserPrincipal authUser) {
        StoreBranchResponseDto res = storeBranchService.updateBranch(branchId, dto, authUser);
        return ResponseEntity.ok(res);
    }

    @DeleteMapping("/delete/{branchId}")
    public ResponseEntity<String> deleteBranch(
            @PathVariable("branchId") Integer branchId,
            @AuthenticationPrincipal CustomUserPrincipal authUser,
            HttpServletRequest request) {
        storeBranchService.deleteBranch(branchId, authUser, request);
        return ResponseEntity.ok("Branch deleted successfully");
    }

    

}
