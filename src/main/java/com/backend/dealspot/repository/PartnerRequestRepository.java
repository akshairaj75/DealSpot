package com.backend.dealspot.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backend.dealspot.entity.PartnerRequest;
import com.backend.dealspot.enums.PartnerRequestStatus;

public interface PartnerRequestRepository extends JpaRepository<PartnerRequest, Long> {


    List<PartnerRequest> findByStatusOrderByCreatedAtDesc(PartnerRequestStatus status);

    List<PartnerRequest> findAllByOrderByCreatedAtDesc();

    boolean existsByApplicantEmailAndStatus(String email, PartnerRequestStatus status);
}
