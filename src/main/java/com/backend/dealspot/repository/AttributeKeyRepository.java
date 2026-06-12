package com.backend.dealspot.repository;

import com.backend.dealspot.entity.AttributeKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AttributeKeyRepository extends JpaRepository<AttributeKey, Long> {

    boolean existsByAttrKeyEnIgnoreCaseOrAttrKeyArIgnoreCase(String attrKeyEn, String attrKeyAr);
    
}
