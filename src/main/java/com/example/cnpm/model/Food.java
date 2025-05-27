package com.example.cnpm.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Food {
    private Long id;
    private String name;
    private String image;
    private Nutrition nutrition;
    private String serving;
}
