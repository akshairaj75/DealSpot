package com.backend.dealspot.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.backend.dealspot.entity.Flyer;

public interface FlyerRepository extends JpaRepository<Flyer, Integer> {
    List<Flyer> findByStoreId(Integer storeId);
}

