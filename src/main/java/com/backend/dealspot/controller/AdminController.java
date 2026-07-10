package com.backend.dealspot.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dealspot/admin")
public class AdminController {

    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('CONTENT_MANAGER')")
    @PostMapping("/offers")
    public ResponseEntity<?> createOffer() {
        return ResponseEntity.ok("Offer created successfully");
    }

    @PostMapping("/admin/cities")
    public ResponseEntity<?> createCity() {
        return ResponseEntity.ok("City created successfully");
    }

    



    // @PostMapping("/stores/{storeId}/follow")
    // public ResponseEntity<?> followStore(
    //         @PathVariable Long storeId,
    //         @AuthenticationPrincipal CustomUserPrincipal authUser) {

    //     if (!authUser.isNormalUser()) {
    //         return ResponseEntity.status(403).body("Only users can follow stores");
    //     }

    //     storeFollowService.follow(authUser.getId(), storeId);
    //     return ResponseEntity.ok("Store followed");
    // }
}