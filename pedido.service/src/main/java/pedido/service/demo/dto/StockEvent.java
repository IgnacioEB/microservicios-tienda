package pedido.service.demo.dto;

public record StockEvent(Long pedidoId, Long productoId, Integer cantidad) {}