package com.example.cnpm.service;

import com.example.cnpm.dto.FoodDTO;

import java.util.List;

public interface FoodService {
    List<FoodDTO> searchFoods(String query);
    FoodDTO getFoodDetails(Long id);
}

