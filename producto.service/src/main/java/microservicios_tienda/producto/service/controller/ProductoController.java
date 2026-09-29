package microservicios_tienda.producto.service.controller;


import microservicios_tienda.producto.service.dto.DescontarStockRequest;
import microservicios_tienda.producto.service.model.Producto;
import microservicios_tienda.producto.service.service.ProductoServicio;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
public class ProductoController {

    private final ProductoServicio productoServicio;

    public ProductoController(ProductoServicio productoServicio){
        this.productoServicio=productoServicio;
    }

    @PostMapping("/producto")
    public ResponseEntity<Producto> crearProducto(@RequestParam String nombre, @RequestParam Double precio, @RequestParam Integer stock){
        return ResponseEntity.status(HttpStatus.CREATED) .body(productoServicio.crearProducto(nombre,precio,stock));
    }

    @GetMapping("/producto/{id}")
    public ResponseEntity<Producto> obtenerProducto(@PathVariable  Long id){
        return ResponseEntity.ok(productoServicio.obtenerProducto(id));
    }

    @GetMapping("/productos")
    public ResponseEntity<ArrayList<Producto>> obtenerProductos(){
        return ResponseEntity.ok(productoServicio.obtenerProductos());
    }

    @PatchMapping("/productos/{id}/stock")
    public ResponseEntity<Producto> descontarStock(@PathVariable Long id, @RequestBody DescontarStockRequest request){
        return ResponseEntity.ok(productoServicio.descontarStock(id, request.getCantidad()));
    }

    @PatchMapping("/productos/{id}/desactivar")
    public ResponseEntity<Producto> desactivar(@PathVariable Long id){
        return ResponseEntity.ok(productoServicio.desactivar(id));
    }






}
