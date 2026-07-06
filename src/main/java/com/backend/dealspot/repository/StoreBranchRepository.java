package com.backend.dealspot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.backend.dealspot.entity.StoreBranch;

public interface StoreBranchRepository extends JpaRepository<StoreBranch, Integer> {
}
