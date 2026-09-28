package pedido.service.demo.exception;

public class ProductoNotAvailableException extends RuntimeException {
    public ProductoNotAvailableException(String message) {
        super(message);
    }
}
