package de.muellermarius.model.values;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Ingredient {
    private final String ingredientName;
    private final int percentageOfMeal;
}
