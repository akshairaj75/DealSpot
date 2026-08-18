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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.backend.dealspot.dto.flyer.FlyerRequestDto;
import com.backend.dealspot.dto.flyer.FlyerResponseDto;
import com.backend.dealspot.dto.flyer.FlyerResponseDto.FlyerPageResponseDto;
import com.backend.dealspot.security.CustomUserPrincipal;
import com.backend.dealspot.service.FlyerService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/flyers")
public class FlyerController {

    private final FlyerService flyerService;

    public FlyerController(FlyerService flyerService) {
        this.flyerService = flyerService;
    }

    @PostMapping("/add")
    public ResponseEntity<FlyerResponseDto> addFlyer(
            @RequestPart("data") FlyerRequestDto flyerRequestDto,
            @RequestPart(value = "pages", required = false) List<MultipartFile> pages,
            @RequestPart(value = "pdf", required = false) MultipartFile pdf,
            @AuthenticationPrincipal CustomUserPrincipal authUser,
            HttpServletRequest request) {
        FlyerResponseDto response = flyerService.addFlyer(flyerRequestDto, pages, pdf, authUser, request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/update/{flyerId}")
    public ResponseEntity<FlyerResponseDto> updateFlyer(
            @PathVariable Integer flyerId,
            @RequestPart("data") FlyerRequestDto flyerRequestDto,
            @RequestPart(value = "pages", required = false) List<MultipartFile> pages,
            @RequestPart(value = "pdf", required = false) MultipartFile pdf,
            @AuthenticationPrincipal CustomUserPrincipal authUser,
            HttpServletRequest request) {
        FlyerResponseDto response = flyerService.updateFlyer(flyerId, flyerRequestDto, pages, pdf, authUser, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/fetch-all-flyers")
    public ResponseEntity<List<FlyerResponseDto>> fetchAllFlyers() {
        List<FlyerResponseDto> list = flyerService.fetchAllFlyers();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/fetch-flyer/{flyerId}")
    public ResponseEntity<FlyerResponseDto> fetchFlyerById(
            @PathVariable Integer flyerId) {
        FlyerResponseDto flyer = flyerService.fetchFlyerById(flyerId);
        return ResponseEntity.ok(flyer);
    }

    @DeleteMapping("/delete/{flyerId}")
    public ResponseEntity<String> deleteFlyer(
            @PathVariable Integer flyerId) {
        flyerService.deleteFlyer(flyerId);
        return ResponseEntity.ok("Flyer deleted successfully");
    }

    // Flyer Pages Endpoints
    @GetMapping("/{flyerId}/pages")
    public ResponseEntity<List<FlyerPageResponseDto>> fetchPagesByFlyerId(
            @PathVariable Integer flyerId) {
        List<FlyerPageResponseDto> pages = flyerService.fetchPagesByFlyerId(flyerId);
        return ResponseEntity.ok(pages);
    }

    @PostMapping("/{flyerId}/pages/add")
    public ResponseEntity<FlyerPageResponseDto> addFlyerPage(
            @PathVariable Integer flyerId,
            @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
            @RequestPart("file") MultipartFile file) {
        FlyerPageResponseDto page = flyerService.addFlyerPage(flyerId, pageNumber, file);
        return ResponseEntity.ok(page);
    }

    @PutMapping("/pages/{pageId}")
    public ResponseEntity<FlyerPageResponseDto> updateFlyerPage(
            @PathVariable Integer pageId,
            @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
            @RequestPart(value = "file", required = false) MultipartFile file) {
        FlyerPageResponseDto page = flyerService.updateFlyerPage(pageId, pageNumber, file);
        return ResponseEntity.ok(page);
    }

    @DeleteMapping("/pages/{pageId}")
    public ResponseEntity<String> deleteFlyerPage(
            @PathVariable Integer pageId) {
        flyerService.deleteFlyerPage(pageId);
        return ResponseEntity.ok("Flyer page deleted successfully");
    }

}
