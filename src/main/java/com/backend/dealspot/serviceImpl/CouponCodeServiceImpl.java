package com.backend.dealspot.serviceImpl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.backend.dealspot.dto.coupon.CouponCodeRequestDto;
import com.backend.dealspot.dto.coupon.CouponCodeResponseDto;
import com.backend.dealspot.entity.CouponCode;
import com.backend.dealspot.entity.Offer;
import com.backend.dealspot.entity.Product;
import com.backend.dealspot.entity.Store;
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
                // AdminUser user = adminUserRepository.findById(authUser.getId())
                //                 .orElseThrow(() -> new RuntimeException("User not found"));

                Offer offer = offerRepository.findById(dto.getOfferId())
                                .orElseThrow(() -> new RuntimeException("Offer not found"));

                Product product = productRepository.findById(dto.getProductId())
                                .orElseThrow(() -> new RuntimeException("Product not found"));

                Store store = storeRepository.findById(dto.getStoreId())
                                .orElseThrow(() -> new RuntimeException("Store not found"));

                CouponCode coupon = new CouponCode();
                coupon.setOffer(offer);
                coupon.setProduct(product);
                coupon.setStore(store);
                coupon.setCode(dto.getCode());
                coupon.setMaxUses(dto.getMaxUses());
                coupon.setDiscountType(dto.getDiscountType());
                coupon.setDiscountValue(dto.getDiscountValue());
                coupon.setMinCartValue(dto.getMinCartValue());
                coupon.setValidFrom(dto.getValidFrom());
                coupon.setValidUntil(dto.getValidUntil());
                coupon.setActive(dto.getActive());

                CouponCode savedCoupon = couponCodeRepository.save(coupon);

                return CouponCodeResponseDto.fromEntity(savedCoupon);
        }

        @Override
        public List<CouponCodeResponseDto> fetchAllCoupon(CustomUserPrincipal authUser, HttpServletRequest request) {
                // AdminUser user = adminUserRepository.findById(authUser.getId())
                //                 .orElseThrow(() -> new RuntimeException("User not found"));

                List<CouponCode> coupons = couponCodeRepository.findAll();
                return coupons.stream().map(CouponCodeResponseDto::fromEntity).toList();
        }

        @Override
        public CouponCodeResponseDto updateCoupon(CouponCodeRequestDto dto, Long couponId, CustomUserPrincipal authUser,
                        HttpServletRequest request) {
                CouponCode coupon = couponCodeRepository.findById(couponId)
                                .orElseThrow(() -> new RuntimeException("Coupon not found"));

                coupon.setCode(dto.getCode());
                coupon.setMaxUses(dto.getMaxUses());
                coupon.setDiscountType(dto.getDiscountType());
                coupon.setDiscountValue(dto.getDiscountValue());
                coupon.setMinCartValue(dto.getMinCartValue());
                coupon.setValidFrom(dto.getValidFrom());
                coupon.setValidUntil(dto.getValidUntil());
                coupon.setActive(dto.getActive());

                CouponCode savedCoupon = couponCodeRepository.save(coupon);

                return CouponCodeResponseDto.fromEntity(savedCoupon);
        }

}
