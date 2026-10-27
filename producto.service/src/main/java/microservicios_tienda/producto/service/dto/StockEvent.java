package microservicios_tienda.producto.service.dto;

public record StockEvent(Long pedidoId, Long productoId, Integer cantidad) {}