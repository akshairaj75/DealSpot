package com.backend.dealspot.serviceImpl;

import java.util.List;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import com.backend.dealspot.dto.coupon.CouponCodeRequestDto;
import com.backend.dealspot.dto.coupon.CouponCodeResponseDto;
import com.backend.dealspot.entity.CouponCode;
import com.backend.dealspot.entity.Offer;
import com.backend.dealspot.entity.Product;
import com.backend.dealspot.entity.Store;
import com.backend.dealspot.enums.AdminRole;
import com.backend.dealspot.repository.CouponCodeRepository;
import com.backend.dealspot.repository.OfferRepository;
import com.backend.dealspot.repository.ProductRepository;
import com.backend.dealspot.repository.StoreRepository;
import com.backend.dealspot.security.CustomUserPrincipal;
import com.backend.dealspot.service.CouponCodeService;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class CouponCodeServiceImpl implements CouponCodeService {

        private final CouponCodeRepository couponCodeRepository;

        private final ProductRepository productRepository;

        private final OfferRepository offerRepository;

        private final StoreRepository storeRepository;

        public CouponCodeServiceImpl(CouponCodeRepository couponCodeRepository,
                        ProductRepository productRepository, OfferRepository offerRepository,
                        StoreRepository storeRepository) {
            this.couponCodeRepository = couponCodeRepository;
            this.productRepository = productRepository;
            this.offerRepository = offerRepository;
            this.storeRepository = storeRepository;
        }

        @Override
        public CouponCodeResponseDto addCoupon(CouponCodeRequestDto dto, CustomUserPrincipal authUser,
                        HttpServletRequest request) {

                if (authUser != null && authUser.getRole() == AdminRole.STORE_MANAGER) {
                        if (authUser.getStoreId() != null) {
                                dto.setStoreId(authUser.getStoreId());
                        }
                }

                Store store = storeRepository.findById(dto.getStoreId())
                                .orElseThrow(() -> new RuntimeException("Store not found"));

                Offer offer = null;
                if (dto.getOfferId() != null) {
                        offer = offerRepository.findById(dto.getOfferId()).orElse(null);
                }

                Product product = null;
                if (dto.getProductId() != null) {
                        product = productRepository.findById(dto.getProductId()).orElse(null);
                }

                CouponCode coupon = new CouponCode();
                coupon.setStore(store);
                coupon.setOffer(offer);
                coupon.setProduct(product);
                coupon.setCode(dto.getCode());
                coupon.setMaxUses(dto.getMaxUses());
                coupon.setUsedCount(dto.getUsedCount() != null ? dto.getUsedCount() : 0);
                coupon.setDiscountType(dto.getDiscountType());
                coupon.setDiscountValue(dto.getDiscountValue());
                coupon.setMinCartValue(dto.getMinCartValue() != null ? dto.getMinCartValue() : java.math.BigDecimal.ZERO);
                coupon.setValidFrom(dto.getValidFrom());
                coupon.setValidUntil(dto.getValidUntil());
                coupon.setActive(dto.getActive() != null ? dto.getActive() : true);

                CouponCode savedCoupon = couponCodeRepository.save(coupon);
                return CouponCodeResponseDto.fromEntity(savedCoupon);
        }

        @Override
        public List<CouponCodeResponseDto> fetchAllCoupon(CustomUserPrincipal authUser, HttpServletRequest request) {
                if (authUser != null && authUser.getRole() == AdminRole.STORE_MANAGER) {
                        if (authUser.getStoreId() != null) {
                                return couponCodeRepository.findByStoreId(authUser.getStoreId())
                                                .stream().map(CouponCodeResponseDto::fromEntity).toList();
                        }
                }
                List<CouponCode> coupons = couponCodeRepository.findAll();
                return coupons.stream().map(CouponCodeResponseDto::fromEntity).toList();
        }

        @Override
        public CouponCodeResponseDto getCouponById(Long couponId) {
                CouponCode coupon = couponCodeRepository.findById(couponId)
                                .orElseThrow(() -> new RuntimeException("Coupon not found"));
                return CouponCodeResponseDto.fromEntity(coupon);
        }

        @Override
        public CouponCodeResponseDto updateCoupon(CouponCodeRequestDto dto, Long couponId, CustomUserPrincipal authUser,
                        HttpServletRequest request) {
                CouponCode coupon = couponCodeRepository.findById(couponId)
                                .orElseThrow(() -> new RuntimeException("Coupon not found"));

                if (authUser != null && authUser.getRole() == AdminRole.STORE_MANAGER) {
                        if (coupon.getStore() == null || !coupon.getStore().getId().equals(authUser.getStoreId())) {
                                throw new AccessDeniedException("You are not authorized to update coupons for another store");
                        }
                        if (authUser.getStoreId() != null) {
                                dto.setStoreId(authUser.getStoreId());
                        }
                }

                if (dto.getStoreId() != null) {
                        storeRepository.findById(dto.getStoreId()).ifPresent(coupon::setStore);
                }

                if (dto.getOfferId() != null) {
                        coupon.setOffer(offerRepository.findById(dto.getOfferId()).orElse(null));
                } else {
                        coupon.setOffer(null);
                }

                if (dto.getProductId() != null) {
                        coupon.setProduct(productRepository.findById(dto.getProductId()).orElse(null));
                } else {
                        coupon.setProduct(null);
                }

                coupon.setCode(dto.getCode());
                coupon.setMaxUses(dto.getMaxUses());
                if (dto.getUsedCount() != null) {
                        coupon.setUsedCount(dto.getUsedCount());
                }
                coupon.setDiscountType(dto.getDiscountType());
                coupon.setDiscountValue(dto.getDiscountValue());
                coupon.setMinCartValue(dto.getMinCartValue() != null ? dto.getMinCartValue() : java.math.BigDecimal.ZERO);
                coupon.setValidFrom(dto.getValidFrom());
                coupon.setValidUntil(dto.getValidUntil());
                if (dto.getActive() != null) {
                        coupon.setActive(dto.getActive());
                }

                CouponCode savedCoupon = couponCodeRepository.save(coupon);
                return CouponCodeResponseDto.fromEntity(savedCoupon);
        }

        @Override
        public void deleteCoupon(Long couponId, CustomUserPrincipal authUser) {
                CouponCode coupon = couponCodeRepository.findById(couponId)
                                .orElseThrow(() -> new RuntimeException("Coupon not found"));

                if (authUser != null && authUser.getRole() == AdminRole.STORE_MANAGER) {
                        if (coupon.getStore() == null || !coupon.getStore().getId().equals(authUser.getStoreId())) {
                                throw new AccessDeniedException("You are not authorized to delete coupons for another store");
                        }
                }

                couponCodeRepository.delete(coupon);
        }

}

