package com.backend.dealspot.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.backend.dealspot.entity.CouponCode;

public interface CouponCodeRepository extends JpaRepository<CouponCode, Long> {
    List<CouponCode> findByStoreId(Integer storeId);
}

