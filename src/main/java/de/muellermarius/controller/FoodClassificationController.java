package de.muellermarius.controller;

import de.muellermarius.model.entities.Meal;
import de.muellermarius.model.values.Ingredient;
import de.muellermarius.repository.IngredientDatabaseMock;
import de.muellermarius.service.FoodClassificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;

@RestController
public class FoodClassificationController {

    private static final Logger LOGGER = LoggerFactory.getLogger(FoodClassificationController.class);

    private final FoodClassificationService foodClassificationService;
    private final IngredientDatabaseMock databaseMock;

    public FoodClassificationController(final FoodClassificationService foodClassificationService, final IngredientDatabaseMock databaseMock) {
        this.foodClassificationService = foodClassificationService;
        this.databaseMock = databaseMock;
    }


    @PostMapping("/api/v1/food/detect")
    public ResponseEntity<Meal> detectFoodOnImage(
            @RequestParam("file") MultipartFile image,
            @RequestParam(name = "mock", defaultValue = "true", required = false) boolean mock
    ) {
        return mock ? createMealMock() : detectFoodOnImage(image);
    }

    private ResponseEntity<Meal> detectFoodOnImage(final MultipartFile image) {
        try {
            final String originalFilename = image.getOriginalFilename();
            final byte[] bytes = image.getBytes();

            final String base64Image = Base64.getEncoder().encodeToString(bytes);

            Meal meal = foodClassificationService.startFoodDetectionProcess(base64Image, originalFilename);

            return ResponseEntity.status(HttpStatus.OK).body(meal);
        } catch (IOException e) {
            LOGGER.error("Fehler beim lesen des Images: " + e.getMessage());
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    private ResponseEntity<Meal> createMealMock() {
        LOGGER.info("Use MealMock");
       final List<Ingredient> ingredients = List.of(
                new Ingredient("lettuce", 30),
                new Ingredient("tomatoes", 20),
                new Ingredient("cucumber", 15),
                new Ingredient("cheese", 10),
                new Ingredient("walnuts", 5),
                new Ingredient("green onions", 5),
                new Ingredient("pumpkin seeds", 5),
                new Ingredient("green dressing", 10)
        );

        final var meal = Meal.builder()
                .aiResponseTimeMs(2500)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .mealClassifier("salad")
                .ingredients(ingredients)
                .build();

        final var totalCalories = ingredients.stream()
                .map(ingredient -> databaseMock.getCalories(ingredient.getIngredientName())
                        .map(calories -> calories * ((double) ingredient.getPercentageOfMeal() / 100))
                        .orElse(0.0))
                .reduce(0.0, Double::sum);

        LOGGER.info("Total Calories per 100g: " + totalCalories);

        return ResponseEntity.status(HttpStatus.OK).body(meal);
    }

}
