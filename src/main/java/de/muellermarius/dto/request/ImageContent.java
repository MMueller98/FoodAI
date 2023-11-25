package de.muellermarius.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ImageContent extends Content{

    @JsonProperty("image_url")
    private ImageUrl imageUrl;

    public ImageContent() {
        this.setType("image_url");
    }

}
