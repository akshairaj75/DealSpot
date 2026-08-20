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

import com.backend.dealspot.dto.coupon.CouponCodeRequestDto;
import com.backend.dealspot.dto.coupon.CouponCodeResponseDto;
import com.backend.dealspot.security.CustomUserPrincipal;
import com.backend.dealspot.service.CouponCodeService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/coupons")
public class CouponCodeController {

    private final CouponCodeService couponService;

    public CouponCodeController(CouponCodeService couponService) {
        this.couponService = couponService;
    }

    @PostMapping("/add-coupon")
    public ResponseEntity<CouponCodeResponseDto> addCoupon(
            @RequestBody CouponCodeRequestDto dto,
            @AuthenticationPrincipal CustomUserPrincipal authUser,
            HttpServletRequest request) {
        CouponCodeResponseDto res = couponService.addCoupon(dto, authUser, request);
        return ResponseEntity.ok(res);
    }

    @GetMapping("/fetch-all")
    public ResponseEntity<List<CouponCodeResponseDto>> fetchAllCoupon(
            @AuthenticationPrincipal CustomUserPrincipal authUser,
            HttpServletRequest request) {
        List<CouponCodeResponseDto> res = couponService.fetchAllCoupon(authUser, request);
        return ResponseEntity.ok(res);
    }

    @GetMapping("/fetch/{couponId}")
    public ResponseEntity<CouponCodeResponseDto> getCouponById(
            @PathVariable Long couponId) {
        CouponCodeResponseDto res = couponService.getCouponById(couponId);
        return ResponseEntity.ok(res);
    }

    @PutMapping("/update/{couponId}")
    public ResponseEntity<CouponCodeResponseDto> updateCoupon(
            @RequestBody CouponCodeRequestDto dto,
            @PathVariable Long couponId,
            @AuthenticationPrincipal CustomUserPrincipal authUser,
            HttpServletRequest request) {
        CouponCodeResponseDto res = couponService.updateCoupon(dto, couponId, authUser, request);
        return ResponseEntity.ok(res);
    }

    @DeleteMapping("/delete/{couponId}")
    public ResponseEntity<String> deleteCoupon(
            @PathVariable Long couponId,
            @AuthenticationPrincipal CustomUserPrincipal authUser) {
        couponService.deleteCoupon(couponId, authUser);
        return ResponseEntity.ok("Coupon deleted successfully");
    }


}
