package backend.academy.linktracker.scrapper.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;

@Getter
@Setter
@Builder
public class AddLinkRequest {
    @NotNull
    @URL
    @NotBlank
    private String link;

    private List<String> tags;
}
