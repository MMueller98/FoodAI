package de.muellermarius.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ImageUrl {
    @JsonProperty("url")
    private String url;

    @JsonProperty("detail")
    private String detail;
}