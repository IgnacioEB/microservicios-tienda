package usuario.service.demo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import usuario.service.demo.dto.UsuarioPutRequest;
import usuario.service.demo.model.Usuario;
import usuario.service.demo.service.UsuarioService;

import java.util.ArrayList;
import java.util.List;

@RestController
public class UsuarioController {
    private UsuarioService usuarioService;
    public UsuarioController(UsuarioService usuarioService){
        this.usuarioService= usuarioService;
    }
    public UsuarioController(){}

    @PostMapping("/usuario")
    public ResponseEntity<String> crearUsuario(@RequestParam String nombre, @RequestParam String email){
        usuarioService.registrarUsuario(nombre,email);
        return ResponseEntity.status(HttpStatus.CREATED) .body("Se creo el usuario exitosamente");
    }

    @GetMapping("/usuario/{id}")
    public ResponseEntity<Usuario> obtenerUsuario(@PathVariable Long id){
        return ResponseEntity.ok(usuarioService.obtenerUsuario(id));
    }
/*
    @GetMapping("/usuarios")
    public ResponseEntity<ArrayList<Usuario>> obtenerTodosLosUsuarios(){
        return ResponseEntity.ok(usuarioService.obtenerUsuarios());
    }

    @PutMapping("/usuario/{id}")
    public ResponseEntity<Usuario> modificarUsuario(@PathVariable Long id, @RequestBody UsuarioPutRequest request){
        return ResponseEntity.ok(usuarioService.modificarUsuario(id,request));
    }

    @DeleteMapping("/usuario/{id}")
    public ResponseEntity<String> eliminarUsuario(@PathVariable Long id){
        return ResponseEntity.status(HttpStatus.OK).body("Se elimino el usuario exitosamente");
    }


*/




}
