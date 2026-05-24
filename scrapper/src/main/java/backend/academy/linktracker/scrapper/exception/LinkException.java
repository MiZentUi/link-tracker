package backend.academy.linktracker.scrapper.exception;

public class LinkException extends RuntimeException {

    public LinkException(String message) {
        super(message);
    }

    public LinkException(Throwable cause) {
        super(cause);
    }
}
