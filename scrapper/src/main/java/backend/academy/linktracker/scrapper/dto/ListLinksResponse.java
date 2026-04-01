package backend.academy.linktracker.scrapper.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;

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
