package de.muellermarius.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class Content {

    @JsonProperty("type")
    private String type;
}
