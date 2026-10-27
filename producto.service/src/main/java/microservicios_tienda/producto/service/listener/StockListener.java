package microservicios_tienda.producto.service.listener;

import microservicios_tienda.producto.service.config.RabbitMQConfig;
import microservicios_tienda.producto.service.dto.PedidoRespuestaEvent;
import microservicios_tienda.producto.service.dto.StockEvent;
import microservicios_tienda.producto.service.service.ProductoService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class StockListener {

    private final ProductoService productoService;
    private final RabbitTemplate rabbitTemplate;

    public StockListener(ProductoService productoService, RabbitTemplate rabbitTemplate) {
        this.productoService = productoService;
        this.rabbitTemplate = rabbitTemplate;
    }

    @RabbitListener(queues = RabbitMQConfig.COLA_STOCK)
    public void procesarDescuento(StockEvent evento) {
        try {
            productoService.descontarStock(evento.productoId(), evento.cantidad());

            PedidoRespuestaEvent respuesta = new PedidoRespuestaEvent(evento.pedidoId(), true);
            rabbitTemplate.convertAndSend(RabbitMQConfig.COLA_RESPUESTA, respuesta);

        } catch (Exception e) {
            PedidoRespuestaEvent respuesta = new PedidoRespuestaEvent(evento.pedidoId(), false);
            rabbitTemplate.convertAndSend(RabbitMQConfig.COLA_RESPUESTA, respuesta);
        }
    }
}