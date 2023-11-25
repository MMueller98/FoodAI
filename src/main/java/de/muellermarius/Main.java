package de.muellermarius;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.muellermarius.dto.request.*;
import de.muellermarius.dto.response.Choice;
import de.muellermarius.dto.response.OpenAiResponse;
import de.muellermarius.dto.response.ResponseMessage;
import de.muellermarius.dto.response.Usage;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

public class Main {
    private static final String API_KEY = System.getenv("API_KEY");
    private static final String OPEN_AI_API_URL = "https://api.openai.com/v1/chat/completions";

    private static final String INPUT_PROMPT = "What food do you recognize in the picture? What do you estimate the calorie count is per 100 grams for the food? Reply in as few words as possible and give an clear estimation, even if you are not 100% confident.";
    private static final String GPT_MODEL = "gpt-4-vision-preview";
    private static final String DETAIL_RESOLUTION = "low";

    private static final int INPUT_TOKEN_PRICE_PER_1K_IN_CENT = 1;
    private static final int OUTPUT_TOKEN_PRICE_PER_1K_IN_CENT = 3;

    private static int counter = 0;

    public static void main(String[] args) {
        startFoodDetectionProcess("kuerbisSuppe.jpg", "Kürbissuppe");
        startFoodDetectionProcess("quinosalat.jpg", "Salat Bowl");
        startFoodDetectionProcess("sandwich.jpg", "Sandwich Tomate Mozarella");
        startFoodDetectionProcess("overnight_oats.jpg", "Overnight Oats mit Himbeeren");
        startFoodDetectionProcess("kimbap.jpg", "Kimbap");
        startFoodDetectionProcess("zuccini_gefuellt.jpg", "Gefüllte Zuccini");
        startFoodDetectionProcess("bayrisch.jpg", "Bayrische Küche");
    }

    private static void startFoodDetectionProcess(final String imagePath, final String imageName) {
        final long startTime = System.currentTimeMillis();
        final OpenAiResponse openAiResponse = detectFoodViaOpenAiApi(imagePath);
        final long endTime = System.currentTimeMillis();

        logResult(imageName, openAiResponse, endTime - startTime);
    }

    private static OpenAiResponse detectFoodViaOpenAiApi(final String imagePath) {

        OpenAiResponse openAiResponse = new OpenAiResponse();

        // Getting the base64 string
        String base64Image = encodeImage(imagePath);

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

    // Function to encode the image
    private static String encodeImage(String imagePath) {
        try {
            // Pfad zum Bild im resources-Ordner
            URL imageUrl = Main.class.getClassLoader().getResource(imagePath);
            if (imageUrl == null) {
                throw new FileNotFoundException("Bild nicht gefunden: " + imagePath);
            }

            File file = new File(imageUrl.getFile());
            byte[] fileContent = new byte[(int) file.length()];
            FileInputStream fis = new FileInputStream(file);
            fis.read(fileContent);
            fis.close();

            return Base64.getEncoder().encodeToString(fileContent);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void logResult(final String imageName, final OpenAiResponse response, final long processTimeMs) {
        System.out.println("\n\n==============================================");
        System.out.println(++counter + ". call to OpenAI API : " + imageName);
        System.out.println("==============================================");

        System.out.println("Process Time : " + processTimeMs + " ms");
        System.out.println();

        System.out.println("GPT-Model: " + response.getModel());
        System.out.println("Detail Resolution: " + DETAIL_RESOLUTION);
        System.out.println();

        System.out.println("Total amount of tokens used: " + Optional.ofNullable(response.getUsage()).map(Usage::getTotalTokens).orElse(0));
        System.out.println("Prompt Tokens: " + Optional.ofNullable(response.getUsage()).map(Usage::getPromptTokens).orElse(0));
        System.out.println("Completion Tokens: " + Optional.ofNullable(response.getUsage()).map(Usage::getCompletionTokens).orElse(0));
        System.out.printf("\nPrice Estimation (cent): %.3f %n", Optional.ofNullable(response.getUsage()).map(Main::calculatePriceEstimation).orElse(0.0));
        System.out.println();

        System.out.println("Prompt: " + INPUT_PROMPT);
        System.out.println("Response: " + getContentOfFirstChoice(response));
    }

    private static String getContentOfFirstChoice(final OpenAiResponse response) {
        return Optional.ofNullable(response.getChoices())
                .flatMap(choices -> choices.stream().findFirst())
                .map(Choice::getResponseMessage)
                .map(ResponseMessage::getContent)
                .orElse("");
    }

    private static double calculatePriceEstimation(final Usage usage) {
        final double inputCost = (((double) usage.getPromptTokens()) / 1000) * INPUT_TOKEN_PRICE_PER_1K_IN_CENT;
        final double outputCost = (((double) usage.getCompletionTokens()) / 1000) * OUTPUT_TOKEN_PRICE_PER_1K_IN_CENT;

        return inputCost + outputCost;
    }
}
