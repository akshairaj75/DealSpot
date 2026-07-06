package com.backend.dealspot.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.dealspot.dto.city.CityRegisterDto;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;


@RestController
@RequestMapping("/api/dealspot/cities")
public class CityController {

    @PostMapping
    public ResponseEntity<CityRegisterDto> createCity(@RequestBody CityRegisterDto dto) {
        CityRegisterDto createdCity = cityService.createCity(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCity);
    }

}
