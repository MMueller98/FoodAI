package de.muellermarius.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class OpenAiRequest {
    @JsonProperty("model")
    private String model;

    @JsonProperty("messages")
    private List<RequestMessage> requestMessages;

    @JsonProperty("max_tokens")
    private int maxTokens;
}