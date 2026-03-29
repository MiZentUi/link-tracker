package backend.academy.linktracker.bot.exception;

import org.springframework.http.HttpStatusCode;

import backend.academy.linktracker.bot.dto.ApiErrorResponse;
import lombok.Getter;

@Getter
public class ApiErrorException extends RuntimeException {
    private ApiErrorResponse response;
    private HttpStatusCode statusCode;

    public ApiErrorException(ApiErrorResponse response, HttpStatusCode statusCode) {
        this.statusCode = statusCode;
        this.response = response;
        super(response.getExceptionMessage());
    }
}
