package pedido.service.demo.controller;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pedido.service.demo.dto.CrearPedidoRequest;
import pedido.service.demo.model.Pedido;
import pedido.service.demo.service.PedidoService;

import java.util.ArrayList;

// X-User-Id y X-User-Rol los agrega el Gateway a partir del token ya validado
@RestController
public class PedidoController {
    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService){
        this.pedidoService=pedidoService;
    }

    @PostMapping("/pedido")
    public ResponseEntity<Pedido> crearPedido(@RequestHeader("X-User-Id") Long usuarioId,
                                              @RequestBody CrearPedidoRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(pedidoService.crearPedido(usuarioId, request));
    }

    @GetMapping("/pedido/{id}")
    public ResponseEntity<Pedido> buscarPedido(@PathVariable Long id,
                                               @RequestHeader("X-User-Id") Long usuarioId,
                                               @RequestHeader("X-User-Rol") String rol){
        return ResponseEntity.ok(pedidoService.buscarPedido(id, usuarioId, "ADMIN".equals(rol)));
    }

    @GetMapping("/pedidos")
    public ResponseEntity<ArrayList<Pedido>> buscarPedidos(@RequestParam(required = false) Long usuario,
                                                           @RequestHeader("X-User-Id") Long usuarioId,
                                                           @RequestHeader("X-User-Rol") String rol){
        return ResponseEntity.ok(pedidoService.buscarPedidos(usuario, usuarioId, "ADMIN".equals(rol)));
    }

}
