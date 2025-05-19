package com.example.cnpm.components;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class SpoonacularClientApi {

    @Value("${spoonacular.api.key}")
    private String apiKey;

    @Value("${spoonacular.base.url}")
    private String baseUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public String getDishByIngredients(String ingredients, String cuisine) {
        String url = String.format(
                "%s/recipes/complexSearch?includeIngredients=%s&cuisine=%s&number=10&apiKey=%s",
                baseUrl, ingredients, cuisine, apiKey
        );
        return restTemplate.getForObject(url, String.class);
    }

}
