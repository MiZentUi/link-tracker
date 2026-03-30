package backend.academy.linktracker.scrapper.exception;

public class LinkAlreadyExistsException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public LinkAlreadyExistsException(String message) {
        super(message);
    }
}
