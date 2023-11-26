package de.muellermarius.model.entities;

import de.muellermarius.model.values.Ingredient;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Data
public class Meal {

    private final long aiResponseTimeMs;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    private final String mealClassifier;
    private final List<Ingredient> ingredients;
}
