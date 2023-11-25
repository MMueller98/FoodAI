package de.muellermarius.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class FinishDetails {

    @JsonProperty("type")
    private String type;

    @JsonProperty("stop")
    private String stop;
}
