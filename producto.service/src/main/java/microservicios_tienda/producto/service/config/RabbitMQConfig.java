package microservicios_tienda.producto.service.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String COLA_STOCK = "cola_descuento_stock";
    public static final String COLA_RESPUESTA = "cola_respuesta_pedido";

    @Bean
    public Queue colaStock() {
        return new Queue(COLA_STOCK, true);
    }

    @Bean
    public Queue colaRespuesta() {
        return new Queue(COLA_RESPUESTA, true);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }
}