package backend.academy.linktracker.scrapper.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class StackOverflowItemsResponse<T> {
    private List<T> items;

    @JsonProperty("quota_remaining")
    private int quotaRemaining;
}
