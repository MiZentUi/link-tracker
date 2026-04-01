package backend.academy.linktracker.bot.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;

@Getter
@Setter
@AllArgsConstructor
public class RemoveLinkRequest {

    @URL
    @NotEmpty
    private String link;
}
