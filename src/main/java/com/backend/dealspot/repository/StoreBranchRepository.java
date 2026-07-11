package com.backend.dealspot.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backend.dealspot.entity.Store;
import com.backend.dealspot.entity.StoreBranch;

public interface StoreBranchRepository extends JpaRepository<StoreBranch, Integer> {

    List<StoreBranch> findByStore(Store store);
}
