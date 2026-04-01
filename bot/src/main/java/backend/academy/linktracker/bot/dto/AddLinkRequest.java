package backend.academy.linktracker.bot.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;

@Getter
@Setter
@NoArgsConstructor
public class AddLinkRequest {

    @URL
    @NotBlank
    private String link;

    private List<String> tags;
}
