package usuario.service.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import usuario.service.demo.dto.ErrorResponse;

@RestControllerAdvice
public class UsuarioExceptionHandler {


    @ExceptionHandler(UsuarioNoEncontradoException.class)
    public ResponseEntity<ErrorResponse> manejarUsuarioNoEncontrado(UsuarioNoEncontradoException e){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage(),"Not Found" ,HttpStatus.NOT_FOUND.value()));
    }





}
