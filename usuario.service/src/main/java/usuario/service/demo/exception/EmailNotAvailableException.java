package usuario.service.demo.exception;

public class EmailNotAvailableException extends RuntimeException {
    public EmailNotAvailableException(String message) {
        super(message);
    }
}
