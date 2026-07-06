package com.backend.dealspot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.backend.dealspot.entity.Store;

public interface StoreRepository extends JpaRepository<Store, Integer> {
}
