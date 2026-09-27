package pedido.service.demo.controller;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pedido.service.demo.model.Pedido;
import pedido.service.demo.service.PedidoService;

import java.util.ArrayList;

@RestController
public class PedidoController {
    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService){
        this.pedidoService=pedidoService;
    }

    @PostMapping("/pedido")
    public ResponseEntity<Pedido> crearPedido(@RequestParam Long productoId, @RequestParam Long usuarioId,@RequestParam Integer cantidad){
        return ResponseEntity.status(HttpStatus.CREATED).body(pedidoService.crearPedido(productoId,usuarioId,cantidad));
    }

    @GetMapping("/pedido/{id}")
    public ResponseEntity<Pedido> buscarPedido(@PathVariable Long id){
        return ResponseEntity.ok(pedidoService.buscarPedido(id));
    }

    @GetMapping("/pedidos")
    public ResponseEntity<ArrayList<Pedido>> buscarPedidos(@RequestParam(required = false)Long usuario){
        return ResponseEntity.ok(pedidoService.buscarPedidos(usuario));
    }





}
