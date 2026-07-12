package backend.academy.linktracker.scrapper.exception;

public class ChatAlreadyExistsException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public ChatAlreadyExistsException(String message) {
        super(message);
    }
}
