package de.muellermarius.repository;

import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class IngredientDatabaseMock {

    private static final Map<String, Double> ingredienCalorieMap = new HashMap<>();

    static {
        ingredienCalorieMap.put("lettuce", 11.0);
        ingredienCalorieMap.put("tomatoes", 11.0);
        ingredienCalorieMap.put("cucumber", 117.0);
        ingredienCalorieMap.put("cheese", 284.0);
        ingredienCalorieMap.put("walnuts", 714.0);
        ingredienCalorieMap.put("green onions", 28.0);
        ingredienCalorieMap.put("pumpkin seeds", 565.0);
        ingredienCalorieMap.put("green dressing", 385.0);
    }

    public Optional<Double> getCalories(final String ingredient) {
        return Optional.ofNullable(ingredienCalorieMap.get(ingredient));
    }
}
