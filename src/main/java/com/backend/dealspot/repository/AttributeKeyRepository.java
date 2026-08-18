package com.backend.dealspot.repository;

import com.backend.dealspot.entity.AttributeKey;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttributeKeyRepository extends JpaRepository<AttributeKey, Long> {

    boolean existsByAttrKeyEnIgnoreCaseOrAttrKeyArIgnoreCase(String attrKeyEn, String attrKeyAr);
    
}
