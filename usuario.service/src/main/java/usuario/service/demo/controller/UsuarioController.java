package usuario.service.demo.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import usuario.service.demo.dto.UsuarioPutRequest;
import usuario.service.demo.model.Usuario;
import usuario.service.demo.service.UsuarioService;

import java.util.ArrayList;

// El alta de usuarios se hace por POST /auth/registro (AuthController)
@RestController
public class UsuarioController {
    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/usuario/{id}")
    public ResponseEntity<Usuario> obtenerUsuario(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.obtenerUsuario(id));
    }

    @GetMapping("/usuarios")
    public ResponseEntity<ArrayList<Usuario>> obtenerTodosLosUsuarios() {
        return ResponseEntity.ok(usuarioService.obtenerUsuarios());
    }

    @PutMapping("/usuario/{id}")
    public ResponseEntity<Usuario> modificarUsuario(@PathVariable Long id, @Valid @RequestBody UsuarioPutRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(usuarioService.modificarUsuario(id, request));
    }

    @DeleteMapping("/usuario/{id}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable Long id) {
        usuarioService.eliminarUsuario(id);
        return ResponseEntity.noContent().build();
    }
}
