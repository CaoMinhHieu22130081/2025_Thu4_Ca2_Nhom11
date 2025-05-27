package com.example.cnpm.controller;

import com.example.cnpm.dto.FoodDTO;
import com.example.cnpm.service.FoodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/foods")
public class FoodController {
    private final FoodService foodService;

    @Autowired
    public FoodController(FoodService foodService) {
        this.foodService = foodService;
    }

    @GetMapping("/search")
    public ResponseEntity<List<FoodDTO>> searchFoods(@RequestParam String query) {
        if (query == null || query.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(new ArrayList<>());
        }
        try {
            // Debug
            System.out.println("Controller received query: '" + query + "'");

            List<FoodDTO> foods = foodService.searchFoods(query);

            // Debug
            System.out.println("Search returned " + foods.size() + " results");

            return ResponseEntity.ok(foods);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ArrayList<>());
        }
    }


    @GetMapping("/{id}")
    public ResponseEntity<FoodDTO> getFoodDetails(@PathVariable Long id) {
        try {
            FoodDTO food = foodService.getFoodDetails(id);
            // Debug
            System.out.println("Controller received food name: '" + food.getName() + "'");
            if (food != null) {
                return ResponseEntity.ok(food);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Ứng dụng đang hoạt động");
    }

}

