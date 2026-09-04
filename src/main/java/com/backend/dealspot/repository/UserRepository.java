package com.backend.dealspot.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.backend.dealspot.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmailIgnoreCase(String email);

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query("SELECT u.id FROM User u ORDER BY u.id ASC")
    Page<Long> findAllUserIds(Pageable pageable);
}
