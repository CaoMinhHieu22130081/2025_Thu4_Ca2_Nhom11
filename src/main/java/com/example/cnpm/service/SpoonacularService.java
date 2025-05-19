package com.example.cnpm.service;

import com.example.cnpm.components.SpoonacularClientApi;
import org.springframework.stereotype.Service;

@Service
public class SpoonacularService {
    private final SpoonacularClientApi spoonacularClientApi;

    public SpoonacularService(SpoonacularClientApi spoonacularClientApi) {
        this.spoonacularClientApi = spoonacularClientApi;
    }

    public String getDishesByIngredients(String ingredients, String cuisine) {
        return spoonacularClientApi.getDishByIngredients(ingredients, cuisine);
    }

}
