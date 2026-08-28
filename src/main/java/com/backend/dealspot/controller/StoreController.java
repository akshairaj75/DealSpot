package com.backend.dealspot.controller;

import java.util.List;


import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.backend.dealspot.dto.store.StoreRegisterDto;
import com.backend.dealspot.dto.store.StoreResponseDto;
import com.backend.dealspot.security.CustomUserPrincipal;
import com.backend.dealspot.service.StoreService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/dealspot/stores")
public class StoreController {


    private final StoreService storeService;

    public StoreController(StoreService storeService) {
        this.storeService = storeService;
    }

    @PostMapping("/create")
    public ResponseEntity<StoreResponseDto> createStore(
            @RequestPart("body") StoreRegisterDto dto,
            @RequestPart(value = "file", required = false) MultipartFile file,
            @AuthenticationPrincipal CustomUserPrincipal authUser,
            HttpServletRequest request) {
        StoreResponseDto createdStore = storeService.createStore(dto, file, authUser, request);
        return ResponseEntity.ok(createdStore);
    }


    // @PostMapping("/create/bulk")
    // public ResponseEntity<List<StoreResponseDto>> createStores(
    // @RequestBody List<StoreRegisterDto> dtos,
    // @AuthenticationPrincipal CustomUserPrincipal authUser,
    // HttpServletRequest request) {

    // List<StoreResponseDto> stores = dtos.stream()
    // .map(dto -> storeService.createStore(dto, authUser, request))
    // .toList();

    // return ResponseEntity.ok(stores);
    // }

    @GetMapping("/fetch-all-stores")
    public ResponseEntity<List<StoreResponseDto>> fetchAllStores(
            @AuthenticationPrincipal CustomUserPrincipal authUser,
            HttpServletRequest request) {
        List<StoreResponseDto> allStores = storeService.fetchAllStores(authUser,
                request);
        return ResponseEntity.ok(allStores);
    }

    @GetMapping("/fetch-store/{storeId}")
    public ResponseEntity<StoreResponseDto> fetchStore(
            @PathVariable("storeId") Integer storeId,
            @AuthenticationPrincipal CustomUserPrincipal authUser,
            HttpServletRequest request) {
        StoreResponseDto store = storeService.fetchStore(storeId, authUser, request);
        return ResponseEntity.ok(store);
    }

    @PutMapping("/update-store/{storeId}")
    public ResponseEntity<StoreResponseDto> updateStore(
            @PathVariable("storeId") Integer storeId,
            @RequestPart("body") StoreRegisterDto dto,
            @RequestPart(value = "file", required = false) MultipartFile file,
            @AuthenticationPrincipal CustomUserPrincipal authUser,
            HttpServletRequest request) {
        StoreResponseDto updatedStore = storeService.updateStore(storeId, dto,
            file, authUser, request);
        return ResponseEntity.ok(updatedStore);
    }

    @PutMapping("/toggle-featured/{storeId}")
    public ResponseEntity<StoreResponseDto> toggleFeatured(
            @PathVariable("storeId") Integer storeId,
            @AuthenticationPrincipal CustomUserPrincipal authUser,
            HttpServletRequest request) {
        StoreResponseDto updated = storeService.toggleFeatured(storeId, authUser, request);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/delete-store/{storeId}")
    public ResponseEntity<String> deleteStore(
            @PathVariable("storeId") Integer storeId,
            @AuthenticationPrincipal CustomUserPrincipal authUser,
            HttpServletRequest request) {
        storeService.deleteStore(storeId, authUser, request);
        return ResponseEntity.ok("Store deleted successfully");
    }

}
