package com.backend.dealspot.serviceImpl;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backend.dealspot.dto.store.StoreResponseDto;
import com.backend.dealspot.entity.Store;
import com.backend.dealspot.entity.StoreFollow;
import com.backend.dealspot.entity.User;
import com.backend.dealspot.repository.StoreFollowRepository;
import com.backend.dealspot.repository.StoreRepository;
import com.backend.dealspot.repository.UserRepository;
import com.backend.dealspot.security.CustomUserPrincipal;
import com.backend.dealspot.service.StoreFollowService;

@Service
public class StoreFollowServiceImpl implements StoreFollowService {

    private final StoreFollowRepository storeFollowRepository;
    private final StoreRepository storeRepository;
    private final UserRepository userRepository;

    public StoreFollowServiceImpl(
            StoreFollowRepository storeFollowRepository,
            StoreRepository storeRepository,
            UserRepository userRepository) {
        this.storeFollowRepository = storeFollowRepository;
        this.storeRepository = storeRepository;
        this.userRepository = userRepository;
    }

    private User getAuthenticatedUser(CustomUserPrincipal authUser) {
        if (authUser == null || authUser.getId() == null) {
            throw new AccessDeniedException("User must be authenticated to perform this action");
        }
        return userRepository.findById(authUser.getId())
                .orElseThrow(() -> new IllegalArgumentException("User account not found"));
    }

    @Transactional
    @Override
    public boolean toggleFollow(Integer storeId, CustomUserPrincipal authUser) {
        User user = getAuthenticatedUser(authUser);
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("Store not found with id: " + storeId));

        Optional<StoreFollow> existing = storeFollowRepository.findByUserAndStore(user, store);
        if (existing.isPresent()) {
            storeFollowRepository.delete(existing.get());
            return false; // Unfollowed
        } else {
            StoreFollow follow = new StoreFollow();
            follow.setUser(user);
            follow.setStore(store);
            storeFollowRepository.save(follow);
            return true; // Followed
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<StoreResponseDto> getFollowedStores(CustomUserPrincipal authUser) {
        if (authUser == null || authUser.getId() == null) {
            return Collections.emptyList();
        }
        User user = userRepository.findById(authUser.getId()).orElse(null);
        if (user == null) {
            return Collections.emptyList();
        }

        List<StoreFollow> follows = storeFollowRepository.findByUserOrderByCreatedAtDesc(user);
        return follows.stream()
                .map(f -> {
                    StoreResponseDto dto = StoreResponseDto.fromEntity(f.getStore());
                    if (dto != null) {
                        dto.setFollowed(true);
                        dto.setFollowersCount(storeFollowRepository.countByStore(f.getStore()));
                    }
                    return dto;
                })
                .toList();
    }

    @Transactional(readOnly = true)
    @Override
    public boolean isFollowing(Integer storeId, CustomUserPrincipal authUser) {
        if (authUser == null || authUser.getId() == null) {
            return false;
        }
        User user = userRepository.findById(authUser.getId()).orElse(null);
        if (user == null) {
            return false;
        }
        Store store = storeRepository.findById(storeId).orElse(null);
        if (store == null) {
            return false;
        }
        return storeFollowRepository.existsByUserAndStore(user, store);
    }

    @Transactional(readOnly = true)
    @Override
    public long getFollowerCount(Integer storeId) {
        return storeFollowRepository.countByStoreId(storeId);
    }

}
