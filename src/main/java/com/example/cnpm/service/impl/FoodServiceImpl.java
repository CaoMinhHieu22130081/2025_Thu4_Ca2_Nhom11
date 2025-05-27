package com.example.cnpm.service.impl;

import com.example.cnpm.dto.FoodDTO;
import com.example.cnpm.model.FoodType;
import com.example.cnpm.service.FoodService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class FoodServiceImpl implements FoodService {

    private final RestTemplate restTemplate;
    private final ObjectMapper mapper = new ObjectMapper();

    private static final String BASE = "https://api.spoonacular.com";

    @Value("${spoonacular.api.key}")
    private String apiKey;

    /* ───────────────────── SEARCH  ───────────────────── */
    //4.1.4 Backend gọi spoonacular API để tìm kiếm thực phẩm với từ khóa đã nhập
    @Override
    public List<FoodDTO> searchFoods(String keyword) {
        log.info("Search keyword received: '{}'", keyword);

        String uri = UriComponentsBuilder.fromHttpUrl(BASE + "/food/ingredients/search")
                .queryParam("query", keyword)
                .queryParam("number", 10)
                .queryParam("apiKey", apiKey)
                .encode(StandardCharsets.UTF_8)
                .toUriString();

        log.info("URI built: {}", uri);

        try {
            HttpClient client = HttpClient.newBuilder()
                    .version(HttpClient.Version.HTTP_1_1)
                    .followRedirects(HttpClient.Redirect.NORMAL)
                    .connectTimeout(Duration.ofSeconds(10))
                    .build();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(uri))
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
                    .header("Accept", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String jsonResponse = response.body();
            log.info("Raw JSON response: {}", jsonResponse);

            JsonNode rootNode = mapper.readTree(jsonResponse);
            JsonNode resultsNode = rootNode.path("results");

            List<FoodDTO> results = new ArrayList<>();

            if (resultsNode.isArray()) {
                for (JsonNode result : resultsNode) {
                    long id = result.path("id").asLong();
                    String name = result.path("name").asText();
                    String image = result.path("image").asText();

                    log.info("Found food: id={}, name='{}'", id, name);

                    FoodDTO food = new FoodDTO();
                    food.setId(id);
                    food.setName(name);
                    food.setImage(image);
                    food.setServing("100g");
                    food.setCalories(100);
                    food.setProtein(5);
                    food.setCarbs(20);
                    food.setFat(2);
                    food.setFiber(1);

                    results.add(food);
                }
            }

            return results;
        } catch (Exception e) {
            log.error("Error searching foods: {}", e.getMessage(), e);
            return List.of();
        }
    }

    /* ───────────── DETAIL (có fallbackName) ──────────── */
    @Override
    public FoodDTO getFoodDetails(Long id) {
        log.info("======= REQUEST FOOD DETAILS START: ID={} =======", id);
        FoodDTO result = getFoodDetails(id, "", FoodType.INGREDIENT).orElse(null);
        log.info("======= REQUEST FOOD DETAILS END =======");
        return result;
    }

    @Cacheable(value = "ingredientInfo", key = "#id")
    public Optional<FoodDTO> getFoodDetails(long id,
                                            String fallbackName,
                                            FoodType type) {
        log.info("Getting detailed nutrition for: ID={}, Type={}, FallbackName='{}'", id, type, fallbackName);

        String uri = switch (type) {
            case INGREDIENT -> BASE + "/food/ingredients/" + id
                    + "/information?amount=100&unit=grams&apiKey=" + apiKey;
            case PRODUCT -> BASE + "/food/products/" + id
                    + "?apiKey=" + apiKey;
        };

        log.info("API Request URL (masked key): {}", uri.replaceAll(apiKey, "API_KEY_HIDDEN"));

        try {
            log.info("Sending request to Spoonacular API...");
            String json = restTemplate.getForObject(uri, String.class);
            log.info("Received response from API (length: {} chars)", json.length());
            log.debug("Raw API response: {}", json);

            JsonNode root = mapper.readTree(json);

            FoodDTO dto = new FoodDTO();
            dto.setId(root.path("id").asLong());
            dto.setName(root.path("name").asText());
            dto.setImage(root.path("image").asText());
            dto.setServing("100g");
            dto.setHasNutrition(true);

            log.info("Extracted base info: id={}, name='{}', image='{}'",
                    dto.getId(), dto.getName(), dto.getImage());

            log.info("Processing nutrition information...");
            root.path("nutrition").path("nutrients").forEach(n -> {
                String nName = n.path("name").asText();
                double val   = n.path("amount").asDouble();

                switch (nName) {
                    case "Calories"      -> {
                        dto.setCalories(val);
                        log.info("Nutrition - Calories: {}", val);
                    }
                    case "Protein"       -> {
                        dto.setProtein(val);
                        log.info("Nutrition - Protein: {}g", val);
                    }
                    case "Carbohydrates" -> {
                        dto.setCarbs(val);
                        log.info("Nutrition - Carbs: {}g", val);
                    }
                    case "Fat"           -> {
                        dto.setFat(val);
                        log.info("Nutrition - Fat: {}g", val);
                    }
                    case "Fiber"         -> {
                        dto.setFiber(val);
                        log.info("Nutrition - Fiber: {}g", val);
                    }
                }
            });

            log.info("Successfully processed food details for ID={}: {} calories, {}g protein, {}g carbs, {}g fat, {}g fiber",
                    id, dto.getCalories(), dto.getProtein(), dto.getCarbs(), dto.getFat(), dto.getFiber());

            return Optional.of(dto);
        }
        // Không có macro cho ID này ➜ vẫn trả DTO tối thiểu
        catch (HttpClientErrorException.NotFound nf) {
            log.warn("No nutrition data found for id={} ({}). Using fallback values.", id, type);

            FoodDTO dto = new FoodDTO();
            dto.setId(id);
            dto.setName(fallbackName);      // dùng tên lấy từ kết quả search
            dto.setServing("100g");
            dto.setCalories(100);           // giá trị mặc định
            dto.setProtein(5);
            dto.setCarbs(20);
            dto.setFat(2);
            dto.setFiber(1);
            dto.setHasNutrition(false);     // đánh dấu thiếu macro

            log.info("Created fallback food object: ID={}, Name='{}', Calories={}",
                    dto.getId(), dto.getName(), dto.getCalories());

            return Optional.of(dto);
        }
        // Lỗi khác ➜ bỏ qua
        catch (Exception ex) {
            log.error("Error fetching food details for ID={}: {}", id, ex.getMessage());
            if (ex.getCause() != null) {
                log.error("Caused by: {}", ex.getCause().getMessage());
            }
            log.debug("Full stack trace:", ex);
            return Optional.empty();
        }
    }

    /* records map JSON search */
    // Cập nhật record để bao gồm tất cả các trường
    public record IngredientSearchResponse(
            List<IngredientSearchResult> results,
            int offset,
            int number,
            int totalResults
    ) {}

    public record IngredientSearchResult(long id, String name, String image) {}
}
