package microservicios_tienda.producto.service.service;

import microservicios_tienda.producto.service.dto.DescontarStockRequest;
import microservicios_tienda.producto.service.exception.ProductoNoEncontradoException;
import microservicios_tienda.producto.service.exception.StockInsuficienteException;
import microservicios_tienda.producto.service.model.Producto;
import microservicios_tienda.producto.service.repository.ProductoRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;


@Service
public class ProductoServicio {

    private final ProductoRepository productoRepository;
    public ProductoServicio(ProductoRepository productoRepository){
        this.productoRepository= productoRepository;
    }


    public Producto crearProducto(String nombre, Double precio, Integer stock) {
        return productoRepository.save(new Producto(nombre,precio,stock));
    }

    public Producto obtenerProducto(Long id) {
        Producto producto= productoRepository.findProductoById(id);
        if(producto==null){
            throw new ProductoNoEncontradoException("Producto no encontrado");
        }
        else{
            return producto;
        }
    }

    public ArrayList<Producto> obtenerProductos() {
        return productoRepository.findAll();
    }




    public Producto descontarStock(Long id, Integer cantidad) {
        Producto producto= productoRepository.findProductoById(id);
        if(producto==null){
            throw new ProductoNoEncontradoException("Producto no encontrado");
        }
        if(cantidad<=0){
            throw new IllegalArgumentException("La cantidad debe ser mayor a 0");
        }
        if(producto.getStock()<cantidad){
            throw new StockInsuficienteException("Stock insuficiente para: "+ producto.getNombre());
        }
        productoRepository.descontarStock(id,cantidad);
        return productoRepository.findProductoById(id);
    }

    public Producto desactivar(Long id) {
        if(productoRepository.findProductoById(id)==null){
            throw new ProductoNoEncontradoException("Producto no encontrado");
        }
        productoRepository.desactivar(id);
        return productoRepository.findProductoById(id);
    }
}
