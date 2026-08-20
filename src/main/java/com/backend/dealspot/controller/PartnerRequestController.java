package com.backend.dealspot.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.backend.dealspot.dto.partner.PartnerApplyRequestDto;
import com.backend.dealspot.dto.partner.PartnerRequestResponseDto;
import com.backend.dealspot.enums.PartnerRequestStatus;
import com.backend.dealspot.service.PartnerRequestService;

@RestController
@RequestMapping("/api/dealspot")
public class PartnerRequestController {

    private final PartnerRequestService partnerRequestService;

    public PartnerRequestController(PartnerRequestService partnerRequestService) {
        this.partnerRequestService = partnerRequestService;
    }

    // Public / User partner application submission
    @PostMapping("/partner-requests/apply")
    public ResponseEntity<PartnerRequestResponseDto> apply(@RequestBody PartnerApplyRequestDto dto) {
        return ResponseEntity.ok(partnerRequestService.submitApplication(dto));
    }

    // Admin list all applications
    @GetMapping("/admin/partner-requests")
    public ResponseEntity<List<PartnerRequestResponseDto>> getAllRequests(
            @RequestParam(value = "status", required = false) PartnerRequestStatus status) {
        return ResponseEntity.ok(partnerRequestService.getAllRequests(status));
    }

    // Admin get single application
    @GetMapping("/admin/partner-requests/{id}")
    public ResponseEntity<PartnerRequestResponseDto> getRequestById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(partnerRequestService.getRequestById(id));
    }

    // Super Admin Approve application
    @PostMapping("/admin/partner-requests/{id}/approve")
    public ResponseEntity<PartnerRequestResponseDto> approveRequest(@PathVariable("id") Long id) {
        return ResponseEntity.ok(partnerRequestService.approveRequest(id));
    }

    // Super Admin Reject application
    @PostMapping("/admin/partner-requests/{id}/reject")
    public ResponseEntity<PartnerRequestResponseDto> rejectRequest(
            @PathVariable("id") Long id,
            @RequestBody(required = false) Map<String, String> body) {
        String reason = body != null ? body.get("reason") : "Application details did not meet DealSpot requirements";
        return ResponseEntity.ok(partnerRequestService.rejectRequest(id, reason));
    }
}
