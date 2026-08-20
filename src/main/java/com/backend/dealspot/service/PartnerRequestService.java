package com.backend.dealspot.service;

import java.util.List;

import com.backend.dealspot.dto.partner.PartnerApplyRequestDto;
import com.backend.dealspot.dto.partner.PartnerRequestResponseDto;
import com.backend.dealspot.enums.PartnerRequestStatus;

public interface PartnerRequestService {

    PartnerRequestResponseDto submitApplication(PartnerApplyRequestDto dto);

    List<PartnerRequestResponseDto> getAllRequests(PartnerRequestStatus status);

    PartnerRequestResponseDto getRequestById(Long id);

    PartnerRequestResponseDto approveRequest(Long id);

    PartnerRequestResponseDto rejectRequest(Long id, String reason);
}
