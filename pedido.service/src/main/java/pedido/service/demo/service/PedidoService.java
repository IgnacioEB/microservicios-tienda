package pedido.service.demo.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import pedido.service.demo.config.RabbitMQConfig;
import pedido.service.demo.dto.*;
import pedido.service.demo.exception.AccesoDenegadoException;
import pedido.service.demo.exception.PedidoNotFoundException;
import pedido.service.demo.exception.ProductoNotAvailableException;
import pedido.service.demo.exception.ProductoNotFoundException;
import pedido.service.demo.exception.StockInsuficienteException;
import pedido.service.demo.exception.UsuarioNotFoundException;
import pedido.service.demo.model.Pedido;
import pedido.service.demo.repository.PedidoRepository;

import java.util.ArrayList;
@Service
public class PedidoService {
    private final PedidoRepository pedidoRepository;
    private final RestTemplate restTemplate;
    private final RabbitTemplate rabbitTemplate;

    private static final String ID_USUARIO= "http://usuario-service:8080/usuario/{id}";
    private static final String ID_PRODUCTO= "http://producto-service:8080/producto/{id}";
    private static final String STOCK_PRODUCTO="http://producto-service:8080/productos/{id}/stock";


    public PedidoService(PedidoRepository pedidoRepository, RestTemplate restTemplate, RabbitTemplate rabbitTemplate){
        this.pedidoRepository=pedidoRepository;
        this.restTemplate = restTemplate;
        this.rabbitTemplate=rabbitTemplate;
    }



    public Pedido crearPedido(Long usuarioId, CrearPedidoRequest request) {
        ProductoDTO producto;
        try {
            producto = restTemplate.getForObject(ID_PRODUCTO, ProductoDTO.class, request.productoId());
        } catch (HttpClientErrorException.NotFound e) {
            throw new ProductoNotFoundException("El producto no existe");
        }

        if (Boolean.FALSE.equals(producto.estado())) {
            throw new ProductoNotAvailableException("El producto no esta disponible");
        }

        try {
            restTemplate.getForObject(ID_USUARIO, UsuarioDTO.class, usuarioId);
        } catch (HttpClientErrorException.NotFound e) {
            throw new UsuarioNotFoundException("El usuario no existe");
        }

        if (request.cantidad() == null || request.cantidad() <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que 0");
        }

        if (producto.stock() < request.cantidad()) {
            throw new StockInsuficienteException("Stock insuficiente");
        }

        Pedido pedido = new Pedido(request.productoId(), usuarioId, request.cantidad());
        pedido.setEstado("PENDIENTE");
        Pedido pedidoGuardado = pedidoRepository.save(pedido);

        StockEvent evento = new StockEvent(
                pedidoGuardado.getId(),
                request.productoId(),
                request.cantidad()
        );
        rabbitTemplate.convertAndSend(RabbitMQConfig.COLA_STOCK, evento);

        // 4. Retornar el pedido en estado pendiente
        return pedidoGuardado;
    }

    public Pedido buscarPedido(Long id, Long usuarioId, boolean esAdmin) {
        Pedido pedido = pedidoRepository.findPedidoById(id);
        if (pedido == null || (!esAdmin && !pedido.getUsuarioId().equals(usuarioId))) {
            throw new PedidoNotFoundException("El pedido no existe");
        }
        return pedido;
    }

    public ArrayList<Pedido> buscarPedidoPorUsuario(Long id) {
        return pedidoRepository.findPedidoByUsuarioId(id);
    }

    // ADMIN: todos (o los de un usuario si manda el filtro). USER: solo los suyos.
    public ArrayList<Pedido> buscarPedidos(Long usuarioFiltro, Long usuarioId, boolean esAdmin) {
        if (esAdmin) {
            return (usuarioFiltro != null)
                    ? this.buscarPedidoPorUsuario(usuarioFiltro)
                    : pedidoRepository.findAll();
        }
        if (usuarioFiltro != null && !usuarioFiltro.equals(usuarioId)) {
            throw new AccesoDenegadoException("No podes ver los pedidos de otro usuario");
        }
        return this.buscarPedidoPorUsuario(usuarioId);
    }

}
