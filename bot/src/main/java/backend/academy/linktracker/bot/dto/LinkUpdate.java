package backend.academy.linktracker.bot.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;

@Getter
@Setter
public class LinkUpdate {

    @NotNull(message = "ID mustn't be null")
    @Min(value = 1, message = "ID must me more than 0")
    private Long id;

    @URL(message = "Not valid URL")
    @NotBlank(message = "URL can't be blank")
    private String url;

    private String description;

    @NotEmpty(message = "One tgchat id minimum requered")
    private List<Long> tgChatIds;
}
