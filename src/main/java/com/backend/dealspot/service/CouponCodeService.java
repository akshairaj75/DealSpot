package com.backend.dealspot.service;

import java.util.List;

import com.backend.dealspot.dto.coupon.CouponCodeRequestDto;
import com.backend.dealspot.dto.coupon.CouponCodeResponseDto;
import com.backend.dealspot.security.CustomUserPrincipal;

import jakarta.servlet.http.HttpServletRequest;

public interface CouponCodeService {

    CouponCodeResponseDto addCoupon(CouponCodeRequestDto dto, CustomUserPrincipal authUser, HttpServletRequest request);

    List<CouponCodeResponseDto> fetchAllCoupon(CustomUserPrincipal authUser, HttpServletRequest request);

    CouponCodeResponseDto updateCoupon(CouponCodeRequestDto dto, Long couponId, CustomUserPrincipal authUser,
            HttpServletRequest request);

    CouponCodeResponseDto getCouponById(Long couponId);

    void deleteCoupon(Long couponId, CustomUserPrincipal authUser);
}

