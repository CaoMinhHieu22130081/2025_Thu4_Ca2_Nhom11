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

    //4.1.3 Hệ thống gửi request đến backend để tìm kiếm thực phẩm
    @GetMapping("/search")
    public ResponseEntity<List<FoodDTO>> searchFoods(@RequestParam String query) {
        if (query == null || query.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(new ArrayList<>());
        }
        try {
            System.out.println("Controller received query: '" + query + "'");

            //4.1.4 Backend gọi spoonacular API để tìm kiếm thực phẩm với từ khóa đã nhập
            List<FoodDTO> foods = foodService.searchFoods(query);

            System.out.println("Search returned " + foods.size() + " results");

            //4.1.5 Backend trả về danh sách các thực phẩm phù hợp
            return ResponseEntity.ok(foods);
        } catch (Exception e) {
            e.printStackTrace();
            //4.2.5 Backend trả về danh sách rỗng
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ArrayList<>());
        }
    }

    //4.1.9 Hệ thống gửi request đến backend để lấy thông tin dinh dưỡng
    @GetMapping("/{id}")
    public ResponseEntity<FoodDTO> getFoodDetails(@PathVariable Long id) {
        try {
            //4.1.10 Backend gọi api để lấy thông tin dinh dưỡng chi tiết của món ăn
            FoodDTO food = foodService.getFoodDetails(id);

            System.out.println("Controller received food name: '" + food.getName() + "'");
            if (food != null) {
                //4.1.11 Backend trả về thông tin dinh dưỡng chi tiết của món ăn
                return ResponseEntity.ok(food);
            } else {
                //4.3.11 Backend trả về không có dữ liệu dinh dưỡng
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

