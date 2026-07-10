package com.backend.dealspot.serviceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.backend.dealspot.dto.store.StoreBranchResponseDto;
import com.backend.dealspot.entity.StoreBranch;
import com.backend.dealspot.repository.StoreBranchRepository;
import com.backend.dealspot.security.CustomUserPrincipal;
import com.backend.dealspot.service.StoreBranchService;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class StoreBranchServiceImpl implements StoreBranchService{

    @Autowired
    StoreBranchRepository storeBranchRepository;


}
