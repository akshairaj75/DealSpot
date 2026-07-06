package com.backend.dealspot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.backend.dealspot.entity.StoreFollow;

public interface StoreFollowRepository extends JpaRepository<StoreFollow, Long> {
}
