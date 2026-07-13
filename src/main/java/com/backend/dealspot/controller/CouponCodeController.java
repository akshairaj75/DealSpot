package com.backend.dealspot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.dealspot.dto.coupon.CouponCodeRequestDto;
import com.backend.dealspot.dto.coupon.CouponCodeResponseDto;
import com.backend.dealspot.security.CustomUserPrincipal;
import com.backend.dealspot.service.CouponCodeService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/coupons")
public class CouponCodeController {

    @Autowired
    CouponCodeService couponService;

    @PostMapping("/add-coupon")
    public ResponseEntity<CouponCodeResponseDto> addCoupon(
            @RequestBody CouponCodeRequestDto dto,
            @AuthenticationPrincipal CustomUserPrincipal authUser,
            HttpServletRequest request) {
        CouponCodeResponseDto res = couponService.addCoupon(dto, authUser, request);
        return ResponseEntity.ok(res);
    }

}
