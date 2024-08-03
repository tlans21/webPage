package HomePage.exception;

import org.springframework.security.authentication.BadCredentialsException;

public class CustomBadCredentialsException extends BadCredentialsException {
    public CustomBadCredentialsException() {
       super("Bad credentials");
    }
    public CustomBadCredentialsException(String message) {
        super(message);
    }

    public CustomBadCredentialsException(String message, Throwable cause) {
        super(message, cause);
    }
}
