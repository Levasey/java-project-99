package hexlet.code.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ReferencedEntityException extends RuntimeException {

    public ReferencedEntityException(String message) {
        super(message);
    }

    public ReferencedEntityException(String message, Throwable cause) {
        super(message, cause);
    }
}
