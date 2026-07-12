package backend.academy.linktracker.bot.exception;

import backend.academy.linktracker.bot.dto.ApiErrorResponse;
import lombok.Getter;
import org.springframework.http.HttpStatusCode;

@Getter
public class ApiServerErrorException extends RuntimeException {
    private final ApiErrorResponse response;
    private final HttpStatusCode statusCode;

    public ApiServerErrorException(ApiErrorResponse response, HttpStatusCode statusCode) {
        this.statusCode = statusCode;
        this.response = response;
        super(response.getExceptionMessage());
    }
}
