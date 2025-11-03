package hexlet.code.handler;

import hexlet.code.exception.ResourceAlreadyExistsException;
import hexlet.code.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler();

    @Test
    void testHandleResourceNotFoundException() {
        var exception = new ResourceNotFoundException("User not found");
        ResponseEntity<String> response = exceptionHandler.handleNotFound(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isEqualTo("User not found");
    }

    @Test
    void testHandleResourceAlreadyExistsException() {
        var exception = new ResourceAlreadyExistsException("User already exists");
        ResponseEntity<String> response = exceptionHandler.handleResourceAlreadyExistsException(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isEqualTo("User already exists");
    }

    @Test
    void testHandleBadCredentialsException() {
        var exception = new BadCredentialsException("Invalid credentials");
        ResponseEntity<String> response = exceptionHandler.handleBadCredentials(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isEqualTo("Invalid credentials");
    }

    @Test
    void testHandleGenericException() {
        var exception = new RuntimeException("Unexpected error");
        ResponseEntity<String> response = exceptionHandler.handleOtherExceptions(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).contains("Something went wrong");
    }
}