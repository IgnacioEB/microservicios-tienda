package pedido.service.demo.exception;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pedido.service.demo.dto.ErrorResponse;

@RestControllerAdvice
public class PedidoExceptionHandler {

    @ExceptionHandler(ProductoNotFoundException.class)
    public ResponseEntity<ErrorResponse> manejarProductoNoExistente(ProductoNotFoundException e){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage(),"Not Found",HttpStatus.NOT_FOUND.value()));
    }
    @ExceptionHandler(UsuarioNotFoundException.class)
    public ResponseEntity<ErrorResponse> manejarUsuarioNoExistente(UsuarioNotFoundException e){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage(),"Not Found",HttpStatus.NOT_FOUND.value()));
    }

    @ExceptionHandler(StockInsuficienteException.class)
    public ResponseEntity<ErrorResponse> manejarStockInsuficiente(StockInsuficienteException e){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(e.getMessage(),"Bad Request",HttpStatus.BAD_REQUEST.value()));
    }
    @ExceptionHandler(ProductoNotAvailableException.class)
    public ResponseEntity<ErrorResponse> manejarProductoNoDisponible(ProductoNotAvailableException e){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(e.getMessage(),"Bad Request",HttpStatus.BAD_REQUEST.value()));
    }
}
