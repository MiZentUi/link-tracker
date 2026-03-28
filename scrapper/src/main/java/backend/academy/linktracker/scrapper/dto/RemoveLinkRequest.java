package backend.academy.linktracker.scrapper.dto;

import org.hibernate.validator.constraints.URL;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RemoveLinkRequest {

    @URL
    @NotEmpty
    private String link;
}