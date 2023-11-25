package de.muellermarius.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class OpenAiResponse {
    @JsonProperty("id")
    private String id;

    @JsonProperty("object")
    private String object;

    @JsonProperty("created")
    private Long created;

    @JsonProperty("model")
    private String model;

    @JsonProperty("usage")
    private Usage usage;

    @JsonProperty("choices")
    private List<Choice> choices;
}
