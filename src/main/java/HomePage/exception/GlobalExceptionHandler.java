package HomePage.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(JoinException.class)
    public ResponseEntity<Void> handleJoinException(JoinException ex) {
        return new ResponseEntity(HttpStatus.CONFLICT);
    }
    @ExceptionHandler(JoinRequestProcessingTimeoutException.class)
    public ResponseEntity<String> handleJoinRequestProcessingException(JoinRequestProcessingTimeoutException ex) {
        return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).body(ex.getMessage());
    }
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<String> handleNullPointerException(NullPointerException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
    }
    @ExceptionHandler(CustomBadCredentialsException.class)
    public ResponseEntity<String> handleCustomBadCredentialsException(CustomBadCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("자격 증명 실패");
    }
}
