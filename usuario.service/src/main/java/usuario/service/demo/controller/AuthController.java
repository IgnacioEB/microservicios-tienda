package usuario.service.demo.controller;


import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import usuario.service.demo.dto.LoginRequest;
import usuario.service.demo.dto.RegistroRequest;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService){
        this.authService=authService;
    }

    @PostMapping("/registro")
    public ResponseEntity<Void> registrar(@Valid @RequestBody RegistroRequest request){
        authService.registrarUsuario(request);
        ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest request){
        ResponseEntity.ok(authService.login(request));
    }




}
