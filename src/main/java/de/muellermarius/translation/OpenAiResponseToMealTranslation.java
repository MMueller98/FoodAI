package de.muellermarius.translation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.muellermarius.dto.IngredientDto;
import de.muellermarius.dto.MealDto;
import de.muellermarius.dto.response.Choice;
import de.muellermarius.dto.response.OpenAiResponse;
import de.muellermarius.dto.response.ResponseMessage;
import de.muellermarius.model.entities.Meal;
import de.muellermarius.model.values.Ingredient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class OpenAiResponseToMealTranslation {

    final Logger LOGGER = LoggerFactory.getLogger(OpenAiResponseToMealTranslation.class);

    public Optional<Meal> translate(final OpenAiResponse response, final long aiResponseTime) {
        return parseContentOfFirstChoice(response)
                .map(dto -> translateDtoToMeal(dto, aiResponseTime));
    }

    private Meal translateDtoToMeal(final MealDto mealDto, final long aiResponseTime) {
        final var mealBuilder = Meal.builder();

        final var ingredients = mealDto.getIngredientDtos().stream().map(this::translateDtoToIngredient).toList();

        return mealBuilder
                .mealClassifier(mealDto.getMealClassifier())
                .aiResponseTimeMs(aiResponseTime)
                .ingredients(ingredients)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    private Ingredient translateDtoToIngredient(final IngredientDto ingredientDto) {
        final var ingredientBuilder = Ingredient.builder();

        return ingredientBuilder
                .ingredientName(ingredientDto.getIngredientName())
                .percentageOfMeal(ingredientDto.getPercentageOfMeal())
                .build();
    }

    private Optional<MealDto> parseContentOfFirstChoice(final OpenAiResponse response) {
        return getContentOfFirstChoice(response).flatMap(this::parseFirstChoiceToMealDto);
    }

    private Optional<String> getContentOfFirstChoice(final OpenAiResponse response) {
        return Optional.ofNullable(response.getChoices())
                .flatMap(choices -> choices.stream().findFirst())
                .map(Choice::getResponseMessage)
                .map(ResponseMessage::getContent);
    }

    private Optional<MealDto> parseFirstChoiceToMealDto(final String firstChoiceResponseContent) {
        final ObjectMapper objectMapper = new ObjectMapper();

        // TODO: wenn parsen fehlschlägt, weitersuchen
        int startIndex = firstChoiceResponseContent.indexOf("{");
        int endIndex = firstChoiceResponseContent.lastIndexOf("}");

        if (startIndex >= 0 && endIndex > 0) {
            try {
                return Optional.of(objectMapper.readValue(firstChoiceResponseContent.substring(startIndex, endIndex+1), MealDto.class));
            } catch (JsonProcessingException e) {
                LOGGER.error("Fehler beim Parsen des firstChoiceResponseContent: " + e.getMessage());
            }
        }

        return Optional.empty();
    }


}
