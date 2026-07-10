package com.backend.dealspot.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.dealspot.dto.store.StoreBranchResponseDto;
import com.backend.dealspot.security.CustomUserPrincipal;
import com.backend.dealspot.service.StoreBranchService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/dealspot/store-branches")
public class StoreBranchController {

    @Autowired
    StoreBranchService storeBranchService;

    @GetMapping("/fetch-all")
    public ResponseEntity<List<StoreBranchResponseDto>> fetchAllStoreBranches(
            @AuthenticationPrincipal CustomUserPrincipal authUser,
            HttpServletRequest request) {
        List<StoreBranchResponseDto> dto = storeBranchService.fetchAllStoreBranches(authUser, request);
        return ResponseEntity.ok(dto);
    }

}
