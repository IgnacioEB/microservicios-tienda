package microservicios_tienda.producto.service.exception;


import microservicios_tienda.producto.service.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ProductoExceptionHandler {

    @ExceptionHandler(ProductoNoEncontradoException.class)
    ResponseEntity<ErrorResponse> manejarProductoNoEncontrado(ProductoNoEncontradoException e){
        return ResponseEntity.status(HttpStatus.NOT_FOUND) .body(new ErrorResponse(e.getMessage(),"Not Found",HttpStatus.NOT_FOUND.value()));
    }

    @ExceptionHandler(StockInsuficienteException.class)
    ResponseEntity<ErrorResponse> manejarStockInsuficiente(StockInsuficienteException e){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(e.getMessage(),"Bad Request", HttpStatus.BAD_REQUEST.value()));

    }

    @ExceptionHandler(IllegalArgumentException.class)
    ResponseEntity<ErrorResponse> manejarIllegalArgument(IllegalArgumentException e){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(e.getMessage(), "Bad Request",HttpStatus.BAD_REQUEST.value()));
    }
}
