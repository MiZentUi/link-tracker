package backend.academy.linktracker.bot.dto;

import java.util.List;

import org.hibernate.validator.constraints.URL;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AddLinkRequest {
    @NotNull
    @URL
    @NotBlank
    private String link;

    private List<String> tags;
}