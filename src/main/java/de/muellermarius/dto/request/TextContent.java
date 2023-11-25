package de.muellermarius.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TextContent extends Content{

    @JsonProperty("text")
    private String text;

    public TextContent() {
        this.setType("text");
    }

}
