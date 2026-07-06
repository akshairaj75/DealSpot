package com.backend.dealspot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.backend.dealspot.entity.UserDevice;

public interface UserDeviceRepository extends JpaRepository<UserDevice, Long> {
}
