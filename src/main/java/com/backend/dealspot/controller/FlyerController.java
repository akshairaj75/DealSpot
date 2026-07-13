package com.backend.dealspot.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.backend.dealspot.dto.flyer.FlyerRequestDto;
import com.backend.dealspot.dto.flyer.FlyerResponseDto;
import com.backend.dealspot.security.CustomUserPrincipal;
import com.backend.dealspot.service.FlyerService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/flyers")
public class FlyerController {

    @Autowired
    FlyerService flyerService;

    @PostMapping("/add")
    public FlyerResponseDto addFlyer(
            @RequestPart("data") FlyerRequestDto flyerRequestDto,
            @RequestPart("pages") List<MultipartFile> pages,
            @RequestPart(value = "pdf", required = false) MultipartFile pdf,
            @AuthenticationPrincipal CustomUserPrincipal authUser,
            HttpServletRequest request) {
        return flyerService.addFlyer(flyerRequestDto, pages, pdf, authUser, request);

    }

    @GetMapping("/fetch-all-flyers")
    public List<FlyerResponseDto> fetchAllFlyers() {
        return flyerService.fetchAllFlyers();
    }

    @GetMapping("/fetch-flyer/{flyerId}")
    public FlyerResponseDto fetchFlyerById(
        @PathVariable Integer flyerId) {
        return flyerService.fetchFlyerById(flyerId);
    }

}
