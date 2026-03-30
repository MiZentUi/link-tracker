package backend.academy.linktracker.scrapper.advice;

import backend.academy.linktracker.scrapper.dto.ApiErrorResponse;
import backend.academy.linktracker.scrapper.exception.ChatAlreadyExistsException;
import backend.academy.linktracker.scrapper.exception.ChatNotFoundException;
import backend.academy.linktracker.scrapper.exception.LinkAlreadyExistsException;
import backend.academy.linktracker.scrapper.exception.LinkNotFoundException;
import java.util.Arrays;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({MethodArgumentNotValidException.class, HandlerMethodValidationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrorResponse wrongRequestArguments(Exception exception) {
        return ApiErrorResponse.builder()
                .description("Wrong request arguments")
                .code(HttpStatus.BAD_REQUEST.toString())
                .exceptionName(exception.getClass().getCanonicalName())
                .exceptionMessage(exception.getMessage())
                .stacktrace(Arrays.stream(exception.getStackTrace())
                        .map(StackTraceElement::toString)
                        .toList())
                .build();
    }

    @ExceptionHandler({ChatAlreadyExistsException.class, LinkAlreadyExistsException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiErrorResponse alreadyExists(Exception exception) {
        return ApiErrorResponse.builder()
                .description("Already exists")
                .code(HttpStatus.CONFLICT.toString())
                .exceptionName(exception.getClass().getCanonicalName())
                .exceptionMessage(exception.getMessage())
                .stacktrace(Arrays.stream(exception.getStackTrace())
                        .map(StackTraceElement::toString)
                        .toList())
                .build();
    }

    @ExceptionHandler({ChatNotFoundException.class, LinkNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiErrorResponse notExists(Exception exception) {
        return ApiErrorResponse.builder()
                .description("Not exists")
                .code(HttpStatus.NOT_FOUND.toString())
                .exceptionName(exception.getClass().getCanonicalName())
                .exceptionMessage(exception.getMessage())
                .stacktrace(Arrays.stream(exception.getStackTrace())
                        .map(StackTraceElement::toString)
                        .toList())
                .build();
    }
}
