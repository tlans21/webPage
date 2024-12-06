package HomePage.exception;

public class JoinRequestProcessingTimeoutException extends RuntimeException {
    public JoinRequestProcessingTimeoutException(String message) {
        super(message);
    }

    public JoinRequestProcessingTimeoutException(String message, Throwable cause) {
        super(message, cause);
    }
}
