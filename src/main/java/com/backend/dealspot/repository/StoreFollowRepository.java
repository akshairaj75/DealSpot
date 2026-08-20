package com.backend.dealspot.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.backend.dealspot.entity.Store;
import com.backend.dealspot.entity.StoreFollow;
import com.backend.dealspot.entity.User;

public interface StoreFollowRepository extends JpaRepository<StoreFollow, Long> {

    boolean existsByUserAndStore(User user, Store store);

    Optional<StoreFollow> findByUserAndStore(User user, Store store);

    long countByStore(Store store);

    long countByStoreId(Integer storeId);

    List<StoreFollow> findByUserOrderByCreatedAtDesc(User user);

    @Query("SELECT sf.store.id FROM StoreFollow sf WHERE sf.user.id = :userId")
    Set<Integer> findFollowedStoreIdsByUserId(@Param("userId") Long userId);
}
