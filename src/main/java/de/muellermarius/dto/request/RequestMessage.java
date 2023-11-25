package de.muellermarius.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class RequestMessage {

    @JsonProperty("role")
    private String role;

    @JsonProperty("content")
    private List<Content> content;

}
