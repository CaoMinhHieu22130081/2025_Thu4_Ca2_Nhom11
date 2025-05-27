package com.example.cnpm.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Nutrition {
    private double calories;
    private double protein;
    private double carbs;
    private double fat;
    private double fiber;
}
