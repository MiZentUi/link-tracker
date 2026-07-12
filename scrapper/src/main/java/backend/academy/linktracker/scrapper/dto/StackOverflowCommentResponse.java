package backend.academy.linktracker.scrapper.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class StackOverflowCommentResponse {
    private String body;

    @JsonProperty("creation_date")
    private int creationDate;

    @JsonIgnore
    private String ownerName;

    @JsonProperty("owner")
    @SuppressWarnings("PMD.UnusedPrivateMethod")
    private void owner(Map<String, Object> owner) {
        ownerName = (String) owner.get("display_name");
    }
}
