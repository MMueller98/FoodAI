package de.muellermarius.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.muellermarius.dto.request.*;
import de.muellermarius.dto.response.Choice;
import de.muellermarius.dto.response.OpenAiResponse;
import de.muellermarius.dto.response.ResponseMessage;
import de.muellermarius.dto.response.Usage;
import de.muellermarius.model.entities.Meal;
import de.muellermarius.translation.OpenAiResponseToMealTranslation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class FoodClassificationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(FoodClassificationService.class);
    private static final String LOG_FILE = "evaluation/OPEN_AI_RESPONSE_LOG.txt";

    private static final String API_KEY = System.getenv("API_KEY");
    private static final String OPEN_AI_API_URL = "https://api.openai.com/v1/chat/completions";

    private static final String INPUT_PROMPT = "Classify the food in the image and list all ingredients together with their proportion of the total amount of food as integer, so that the sum of all integers is 100. Return ONLY valid JSON int the format {\"food_classifier\":<string>,\"ingredients\":[{\"name\":<String>,\"portion\":<number>}]}";
    private static final String GPT_MODEL = "gpt-4-vision-preview";
    private static final String DETAIL_RESOLUTION = "low";

    private static final int INPUT_TOKEN_PRICE_PER_1K_IN_CENT = 1;
    private static final int OUTPUT_TOKEN_PRICE_PER_1K_IN_CENT = 3;

    private final OpenAiResponseToMealTranslation openAiResponseToMealTranslation;

    @Autowired
    public FoodClassificationService(final OpenAiResponseToMealTranslation openAiResponseToMealTranslation) {
        this.openAiResponseToMealTranslation = openAiResponseToMealTranslation;
    }

    public Meal startFoodDetectionProcess(final String base64Image, final String imageName) {
        final long startTime = System.currentTimeMillis();
        final OpenAiResponse openAiResponse = detectFoodViaOpenAiApi(base64Image);
        final long duration = System.currentTimeMillis() - startTime;

        logApiResult(imageName, openAiResponse, duration);

        return openAiResponseToMealTranslation
                .translate(openAiResponse, duration)
                .map(this::logSuccessfull)
                .orElse(logFailureWithDefault());
    }

    private OpenAiResponse detectFoodViaOpenAiApi(final String base64Image) {

        OpenAiResponse openAiResponse = new OpenAiResponse();

        // Constructing the request payload using POJOs
        OpenAiRequest requestPayload = new OpenAiRequest();
        requestPayload.setModel(GPT_MODEL);

        RequestMessage userRequestMessage = new RequestMessage();
        userRequestMessage.setRole("user");

        TextContent textContent = new TextContent();
        textContent.setText(INPUT_PROMPT);

        ImageContent imageUrlContent = new ImageContent();
        ImageUrl imageUrl = new ImageUrl();
        imageUrl.setUrl("data:image/jpeg;base64," + base64Image);
        imageUrl.setDetail(DETAIL_RESOLUTION);
        imageUrlContent.setImageUrl(imageUrl);

        userRequestMessage.setContent(List.of(textContent, imageUrlContent));

        requestPayload.setRequestMessages(List.of(userRequestMessage));
        requestPayload.setMaxTokens(300);

        // Setting up the request headers
        String authHeader = "Bearer " + API_KEY;
        String contentTypeHeader = "application/json";

        // Making the HTTP request
        try {
            URL url = new URL(OPEN_AI_API_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", authHeader);
            connection.setRequestProperty("Content-Type", contentTypeHeader);
            connection.setDoOutput(true);

            // Writing the payload to the request body using Jackson
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonPayload = objectMapper.writeValueAsString(requestPayload);

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonPayload.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            // Reading the response
            try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }

                // Parse the JSON response using Jackson
                openAiResponse = objectMapper.readValue(response.toString(), OpenAiResponse.class);

                // Do something with the parsed response
                //System.out.println(openAiResponse.toString());
            }

            // Closing the connection
            connection.disconnect();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return openAiResponse;
    }

    private String getContentOfFirstChoice(final OpenAiResponse response) {
        return Optional.ofNullable(response.getChoices())
                .flatMap(choices -> choices.stream().findFirst())
                .map(Choice::getResponseMessage)
                .map(ResponseMessage::getContent)
                .orElse("");
    }

    private double calculatePriceEstimation(final Usage usage) {
        final double inputCost = (((double) usage.getPromptTokens()) / 1000) * INPUT_TOKEN_PRICE_PER_1K_IN_CENT;
        final double outputCost = (((double) usage.getCompletionTokens()) / 1000) * OUTPUT_TOKEN_PRICE_PER_1K_IN_CENT;

        return inputCost + outputCost;
    }

    public String logApiResult(final String imageName, final OpenAiResponse response, final long processTimeMs) {
        final var header = LocalDateTime.now() + ": Image " + imageName;
        final var newLine = "\n";
        final var divider = "=".repeat(header.length() + 2);

        var logStr = newLine + newLine + divider + newLine + header + newLine + divider + newLine + newLine;


        logStr += "Process Time : " + processTimeMs + " ms";
        logStr += newLine;
        logStr += newLine;

        logStr += "GPT-Model: " + response.getModel();
        logStr += newLine;
        logStr += "Detail Resolution: " + DETAIL_RESOLUTION;
        logStr += newLine;
        logStr += newLine;

        logStr += "Total amount of tokens used: " + Optional.ofNullable(response.getUsage()).map(Usage::getTotalTokens).orElse(0);
        logStr += newLine;
        logStr += "Prompt Tokens: " + Optional.ofNullable(response.getUsage()).map(Usage::getPromptTokens).orElse(0);
        logStr += newLine;
        logStr += "Completion Tokens: " + Optional.ofNullable(response.getUsage()).map(Usage::getCompletionTokens).orElse(0);
        logStr += newLine;
        logStr += newLine;

        logStr += String.format("Price Estimation (cent): %.3f %n", Optional.ofNullable(response.getUsage())
                .map(this::calculatePriceEstimation)
                .orElse(0.0));
        logStr += newLine;

        logStr += "Prompt: " + INPUT_PROMPT;
        logStr += newLine;
        logStr += "Response: " + getContentOfFirstChoice(response);
        logStr += newLine;

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(LOG_FILE, true))) {
            writer.write(logStr);
        } catch (IOException e) {
            LOGGER.error("Fehler beim loggen der OpenAiResponse: " + e.getMessage());
        }

        return logStr;
    }

    private <T> T logSuccessfull(final T entity) {
        LOGGER.info("Translation Successfull! Entity created: {}", entity);

        return entity;
    }

    private Meal logFailureWithDefault() {
        //LOGGER.error("Error in Translation: Missing Information!");

        return Meal.builder().build();
    }
}
