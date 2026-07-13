package com.backend.dealspot.service;

import com.backend.dealspot.dto.coupon.CouponCodeRequestDto;
import com.backend.dealspot.dto.coupon.CouponCodeResponseDto;
import com.backend.dealspot.security.CustomUserPrincipal;

import jakarta.servlet.http.HttpServletRequest;

public interface CouponCodeService {

    CouponCodeResponseDto addCoupon(CouponCodeRequestDto dto, CustomUserPrincipal authUser, HttpServletRequest request);

}
