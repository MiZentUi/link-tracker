package backend.academy.linktracker.scrapper.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;

@Getter
@Setter
@AllArgsConstructor
public class LinkResponse {

    @NotNull(message = "ID mustn't be null")
    @Min(value = 0, message = "ID must be positive number")
    private Long id;

    @URL(message = "Not valid URL")
    @NotBlank(message = "URL can't be blank")
    private String url;

    private List<String> tags;
}
