package backend.academy.linktracker.scrapper.exception;

public class ChatNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public ChatNotFoundException(String message) {
        super(message);
    }
}
