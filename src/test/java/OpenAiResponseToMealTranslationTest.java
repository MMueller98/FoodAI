import de.muellermarius.FoodAiApplication;
import de.muellermarius.dto.response.*;
import de.muellermarius.model.entities.Meal;
import de.muellermarius.translation.OpenAiResponseToMealTranslation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = FoodAiApplication.class)
public class OpenAiResponseToMealTranslationTest {

    @Autowired
    private OpenAiResponseToMealTranslation openAiResponseToMealTranslation;

    @Test
    public void testTranslation() {
        Meal translate = openAiResponseToMealTranslation.translate(createOpenAiResponse(), 3000L).orElse(null);

        System.out.println(translate);
    }

    private OpenAiResponse createOpenAiResponse() {
        final var usage = new Usage();
        usage.setPromptTokens(100);
        usage.setCompletionTokens(100);
        usage.setTotalTokens(200);

        final var finishDetail = new FinishDetails();
        finishDetail.setStop("stop");
        finishDetail.setType("type");

        final var responseMessage = new ResponseMessage();
        responseMessage.setRole("assistant");
        responseMessage.setContent("```json\n" + "{\n" + "  \"food_classifier\": \"salad\",\n" + "  \"ingredients\": [\n" + "    {\"name\": \"lettuce\", \"portion\": 30},\n" + "    {\"name\": \"tomatoes\", \"portion\": 20},\n" + "    {\"name\": \"cucumber\", \"portion\": 15},\n" + "    {\"name\": \"cheese\", \"portion\": 10},\n" + "    {\"name\": \"nuts\", \"portion\": 5},\n" + "    {\"name\": \"green onions\", \"portion\": 5},\n" + "    {\"name\": \"pumpkin seeds\", \"portion\": 5},\n" + "    {\"name\": \"dressing\", \"portion\": 10}\n" + "  ]\n" + "}\n" + "```");

        final var choice = new Choice();
        choice.setIndex(0);
        choice.setFinishDetails(finishDetail);
        choice.setResponseMessage(responseMessage);

        final var openAiResponse = new OpenAiResponse();
        openAiResponse.setId("test_id");
        openAiResponse.setObject("chat.completion");
        openAiResponse.setCreated(System.currentTimeMillis());
        openAiResponse.setModel("gpt-4-1106-vision-preview");
        openAiResponse.setUsage(usage);
        openAiResponse.setChoices(List.of(choice));

        return openAiResponse;
    }
}
