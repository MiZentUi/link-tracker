package backend.academy.linktracker.bot.dto;

import java.util.List;

import org.hibernate.validator.constraints.URL;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

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
