package de.muellermarius.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IngredientDto {

    @JsonProperty("name")
    private String ingredientName;

    @JsonProperty("portion")
    private int percentageOfMeal;
}
