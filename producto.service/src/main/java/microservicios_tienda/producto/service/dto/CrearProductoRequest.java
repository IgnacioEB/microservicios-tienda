package microservicios_tienda.producto.service.dto;

public record CrearProductoRequest(String nombre, Double precio, Integer stock){}