package com.example.cnpm.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FoodDTO {
    private Long   id;
    private String name;
    private String image;
    private String serving;

    private double calories;
    private double protein;
    private double carbs;
    private double fat;
    private double fiber;

    /* true = Spoonacular có macro, false = chỉ có tên/hình */
    private boolean hasNutrition = true;
}
