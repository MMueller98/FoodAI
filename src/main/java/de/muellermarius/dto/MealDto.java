package de.muellermarius.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MealDto {

    @JsonProperty("food_classifier")
    private String mealClassifier;

    @JsonProperty("ingredients")
    private List<IngredientDto> ingredientDtos;
}
