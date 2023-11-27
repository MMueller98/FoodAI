package de.muellermarius.model.values;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class Ingredient {
    private final String ingredientName;
    private final int percentageOfMeal;
}
