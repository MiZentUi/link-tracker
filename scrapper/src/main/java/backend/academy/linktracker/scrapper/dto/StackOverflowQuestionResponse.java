package backend.academy.linktracker.scrapper.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class StackOverflowQuestionResponse {

    @JsonProperty("last_activity_date")
    private int lastActivityDate;

    private String title;
}
