package de.muellermarius.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ResponseMessage {

    @JsonProperty("role")
    private String role;

    @JsonProperty("content")
    private String content;
}
