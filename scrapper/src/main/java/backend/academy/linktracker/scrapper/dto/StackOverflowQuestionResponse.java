package backend.academy.linktracker.scrapper.dto;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class StackOverflowQuestionResponse {
    @JsonProperty("items")
    private void items(List<Map<String, Object>> items) {
        for (var item : items) {
            this.lastActivityDate = (Integer) item.get("last_activity_date");
        }
    }

    @JsonIgnore
    private int lastActivityDate;

    @JsonProperty("quota_remaining")
    private int quotaRemaining;
}
