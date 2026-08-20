package com.backend.dealspot.service;

import java.util.List;

import com.backend.dealspot.dto.store.StoreResponseDto;
import com.backend.dealspot.security.CustomUserPrincipal;

public interface StoreFollowService {

    boolean toggleFollow(Integer storeId, CustomUserPrincipal authUser);

    List<StoreResponseDto> getFollowedStores(CustomUserPrincipal authUser);

    boolean isFollowing(Integer storeId, CustomUserPrincipal authUser);

    long getFollowerCount(Integer storeId);

}
