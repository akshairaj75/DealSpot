package com.backend.dealspot.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.dealspot.dto.store.StoreResponseDto;
import com.backend.dealspot.security.CustomUserPrincipal;
import com.backend.dealspot.service.StoreFollowService;

@RestController
@RequestMapping("/api/dealspot/stores")
public class StoreFollowController {

    private final StoreFollowService storeFollowService;

    public StoreFollowController(StoreFollowService storeFollowService) {
        this.storeFollowService = storeFollowService;
    }

    @PostMapping("/{storeId}/follow-toggle")
    public ResponseEntity<Map<String, Object>> toggleFollow(
            @PathVariable Integer storeId,
            @AuthenticationPrincipal CustomUserPrincipal authUser) {
        boolean isFollowing = storeFollowService.toggleFollow(storeId, authUser);
        long count = storeFollowService.getFollowerCount(storeId);
        return ResponseEntity.ok(Map.of(
                "isFollowing", isFollowing,
                "followersCount", count,
                "message", isFollowing ? "Store followed successfully" : "Store unfollowed successfully"
        ));
    }

    @GetMapping("/my-followed")
    public ResponseEntity<List<StoreResponseDto>> getMyFollowedStores(
            @AuthenticationPrincipal CustomUserPrincipal authUser) {
        List<StoreResponseDto> stores = storeFollowService.getFollowedStores(authUser);
        return ResponseEntity.ok(stores);
    }

    @GetMapping("/{storeId}/is-following")
    public ResponseEntity<Map<String, Boolean>> isFollowing(
            @PathVariable Integer storeId,
            @AuthenticationPrincipal CustomUserPrincipal authUser) {
        boolean isFollowing = storeFollowService.isFollowing(storeId, authUser);
        return ResponseEntity.ok(Map.of("isFollowing", isFollowing));
    }

    @GetMapping("/{storeId}/followers-count")
    public ResponseEntity<Map<String, Long>> getFollowersCount(
            @PathVariable Integer storeId) {
        long count = storeFollowService.getFollowerCount(storeId);
        return ResponseEntity.ok(Map.of("followersCount", count));
    }

}
