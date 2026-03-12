package backend.academy.linktracker.scrapper.dto;

import java.util.List;

import org.hibernate.validator.constraints.URL;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ListLinksResponse {
    @URL
    @NotEmpty
    private List<LinkResponse> links;

    @NotNull
    @Min(value = 0)
    private Integer size;
}
