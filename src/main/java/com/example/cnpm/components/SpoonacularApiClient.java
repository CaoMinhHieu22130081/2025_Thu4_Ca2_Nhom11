package com.example.cnpm.components;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class SpoonacularApiClient {

    @Value("${spoonacular.api.key}")
    private String apiKey;

    @Value("${spoonacular.base.url}")
    private String baseUrl;

    private final RestTemplate restTemplate = new RestTemplate();


    public String fetchDishesFromSpoonacular(String ingredients, String cuisine) {
        //1.1.7 SpoonacularApiClient tạo một request đến Spoonacular API tại endpoint /recipes/complexSearch với các tham số includeIngredients và cuisine.
                String url = String.format(
                "%s/recipes/complexSearch?includeIngredients=%s&cuisine=%s&number=10&apiKey=%s",
                baseUrl, ingredients, cuisine, apiKey
        );
        //1.1.8 Spoonacular API trả về một JSON chứa danh sách món ăn phù hợp với nguyên liệu và quốc gia đã chọn. Backend xử lý dữ liệu trả về và gửi lại danh sách món ăn này cho frontend.
        return restTemplate.getForObject(url, String.class);
    }

}
