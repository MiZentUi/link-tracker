package backend.academy.linktracker.scrapper.dto;

import java.util.List;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ApiErrorResponse {

    @NotNull
    private String description;

    @NotNull
    private String code;

    @NotNull
    private String exceptionName;

    @NotNull
    private String exceptionMessage;
    private List<String> stacktrace;
}
