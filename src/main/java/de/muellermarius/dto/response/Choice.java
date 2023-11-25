package de.muellermarius.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Choice {
    @JsonProperty("message")
    private ResponseMessage responseMessage;

    @JsonProperty("finish_details")
    private FinishDetails finishDetails;

    @JsonProperty("index")
    private int index;
}
