package com.backend.dealspot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.backend.dealspot.entity.City;

public interface CityRepository extends JpaRepository<City, Integer> {
}
