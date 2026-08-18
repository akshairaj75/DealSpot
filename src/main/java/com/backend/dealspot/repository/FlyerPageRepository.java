package com.backend.dealspot.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.backend.dealspot.entity.Flyer;
import com.backend.dealspot.entity.FlyerPage;

public interface FlyerPageRepository extends JpaRepository<FlyerPage, Integer> {
    List<FlyerPage> findByFlyerOrderByPageNumberAsc(Flyer flyer);
    List<FlyerPage> findByFlyerIdOrderByPageNumberAsc(Integer flyerId);
}
