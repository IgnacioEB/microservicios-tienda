package pedido.service.demo.exception;

public class PedidoNotFoundException extends RuntimeException {
    public PedidoNotFoundException(String message) {
        super(message);
    }
}
