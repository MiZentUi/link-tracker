package backend.academy.linktracker.scrapper.dto;

import java.util.List;

import org.hibernate.validator.constraints.URL;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

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