package com.example.cnpm.service;

import com.example.cnpm.components.SpoonacularApiClient;
import org.springframework.stereotype.Service;

@Service
public class SpoonacularService {
    private final SpoonacularApiClient spoonacularApiClient;

    public SpoonacularService(SpoonacularApiClient spoonacularApiClient) {
        this.spoonacularApiClient = spoonacularApiClient;
    }

    //1.1.6	Bên trong SpoonacularService, hệ thống gọi fetchDishesFromSpoonacular để truy xuất dữ liệu món ăn từ  API Spoonacular.
    public String getDishesByIngredientsAndCuisine(String ingredients, String cuisine) {
        return spoonacularApiClient.fetchDishesFromSpoonacular(ingredients, cuisine);
    }

}
