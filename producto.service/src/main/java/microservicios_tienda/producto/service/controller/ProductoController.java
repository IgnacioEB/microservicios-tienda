package microservicios_tienda.producto.service.controller;


import microservicios_tienda.producto.service.dto.CrearProductoRequest;
import microservicios_tienda.producto.service.dto.DescontarStockRequest;
import microservicios_tienda.producto.service.model.Producto;
import microservicios_tienda.producto.service.service.ProductoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService){
        this.productoService = productoService;
    }

    @PostMapping("/producto")
    public ResponseEntity<Producto> crearProducto(@RequestBody CrearProductoRequest request){
        return ResponseEntity.status(HttpStatus.CREATED) .body(productoService.crearProducto(request));
    }

    @GetMapping("/producto/{id}")
    public ResponseEntity<Producto> obtenerProducto(@PathVariable  Long id){
        return ResponseEntity.ok(productoService.obtenerProducto(id));
    }

    @GetMapping("/productos")
    public ResponseEntity<ArrayList<Producto>> obtenerProductos(){
        return ResponseEntity.ok(productoService.obtenerProductos());
    }

    @PatchMapping("/productos/{id}/stock")
    public ResponseEntity<Producto> descontarStock(@PathVariable Long id, @RequestBody DescontarStockRequest request){
        return ResponseEntity.ok(productoService.descontarStock(id, request.cantidad()));
    }

    @PatchMapping("/productos/{id}/desactivar")
    public ResponseEntity<Producto> desactivar(@PathVariable Long id){
        return ResponseEntity.ok(productoService.desactivar(id));
    }






}
