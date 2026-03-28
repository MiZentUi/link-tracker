package backend.academy.linktracker.scrapper.exception;

public class LinkNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public LinkNotFoundException(String message) {
        super(message);
    }
}
