package backend.academy.linktracker.bot.exception;

import backend.academy.linktracker.bot.dto.ApiErrorResponse;
import lombok.Getter;
import org.springframework.http.HttpStatusCode;

@Getter
public class ApiClientErrorException extends RuntimeException {
    private final ApiErrorResponse response;
    private final HttpStatusCode statusCode;

    public ApiClientErrorException(ApiErrorResponse response, HttpStatusCode statusCode) {
        this.statusCode = statusCode;
        this.response = response;
        super(response.getExceptionMessage());
    }
}
