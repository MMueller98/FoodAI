# OpenAI Food Recognition Evaluation Project

This project utilizes the OpenAI API for food recognition in images, with a focus on estimating the type of food and its calorie count per 100 grams. The `Main` class contains a sample implementation that processes multiple food images through the OpenAI API and logs the results.

## Usage

1. Configure your OpenAI API key by setting the `API_KEY` constant in the `Main` class.
2. Choose a specific input prompt, the GPT-Model & and the desired Detail Resolution.
3. Call the `startFoodDetectionProcess` Method to evaluate the OpenAI API for a specific image.

## Evaluation Results

### Kürbissuppe
- Process Time: 2335 ms
- GPT Model: gpt-4-1106-vision-preview
- Detail Resolution: low
- Total Tokens Used: 155
- Prompt Tokens: 140
- Completion Tokens: 15
- Price Estimation: 0.185 cent
- Response: Pumpkin soup, approximately 50-80 kcal/100g.

### Salat Bowl
- Process Time: 1940 ms
- GPT Model: gpt-4-1106-vision-preview
- Detail Resolution: low
- Total Tokens Used: 154
- Prompt Tokens: 140
- Completion Tokens: 14
- Price Estimation: 0.182 cent
- Response: Mixed salad. Calorie count: ~50 kcal/100g.

### Sandwich Tomate Mozarella
- Process Time: 3158 ms
- GPT Model: gpt-4-1106-vision-preview
- Detail Resolution: low
- Total Tokens Used: 155
- Prompt Tokens: 140
- Completion Tokens: 15
- Price Estimation: 0.185 cent
- Response: Caprese sandwich. Estimate: 250-300 kcal/100g.

### Overnight Oats mit Himbeeren
- Process Time: 1784 ms
- GPT Model: gpt-4-1106-vision-preview
- Detail Resolution: low
- Total Tokens Used: 158
- Prompt Tokens: 140
- Completion Tokens: 18
- Price Estimation: 0.194 cent
- Response: Oatmeal with raspberries. Estimate: 110-130 calories/100g.

### Kimbap
- Process Time: 2173 ms
- GPT Model: gpt-4-1106-vision-preview
- Detail Resolution: low
- Total Tokens Used: 153
- Prompt Tokens: 140
- Completion Tokens: 13
- Price Estimation: 0.179 cent
- Response: Sushi. Approximately 130-175 calories/100g.

### Gefüllte Zuccini
- Process Time: 1874 ms
- GPT Model: gpt-4-1106-vision-preview
- Detail Resolution: low
- Total Tokens Used: 153
- Prompt Tokens: 140
- Completion Tokens: 13
- Price Estimation: 0.179 cent
- Response: Stuffed zucchini. Approximately 150 calories/100g.

### Bayrische Küche
- Process Time: 1940 ms
- GPT Model: gpt-4-1106-vision-preview
- Detail Resolution: low
- Total Tokens Used: 158
- Prompt Tokens: 140
- Completion Tokens: 18
- Price Estimation: 0.194 cent
- Response: Roast chicken, potato dumpling, red cabbage. ~250 kcal/100g.

## Latest Evaluation: 25.11.2023
