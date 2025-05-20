package com.example.cnpm.controller;

import com.example.cnpm.service.SpoonacularService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/dish")
@CrossOrigin(origins = "*")
public class SuggestDishController {
    private final SpoonacularService spoonacularService;

    public SuggestDishController(SpoonacularService service) {
        this.spoonacularService = service;
    }

    //1.1.5 Backend nhận request tại controller SuggestDishController, sau đó gọi phương thức getDishesByIngredientsAndCuisine trong SpoonacularService để xử lý logic.
    @GetMapping("/suggest")
    public String suggestDish(
            @RequestParam String ingredients,
            @RequestParam String cuisine
    ) {
        return spoonacularService.getDishesByIngredientsAndCuisine(ingredients, cuisine);
    }
}
