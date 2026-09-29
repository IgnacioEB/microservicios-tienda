package pedido.service.demo.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import pedido.service.demo.dto.CrearPedidoRequest;
import pedido.service.demo.dto.DescontarStockRequest;
import pedido.service.demo.dto.ProductoDTO;
import pedido.service.demo.dto.UsuarioDTO;
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

    private static final String ID_USUARIO= "http://usuario-service:8080/usuario/{id}";
    private static final String ID_PRODUCTO= "http://producto-service:8080/producto/{id}";
    private static final String STOCK_PRODUCTO="http://producto-service:8080/productos/{id}/stock";


    public PedidoService(PedidoRepository pedidoRepository, RestTemplate restTemplate){
        this.pedidoRepository=pedidoRepository;
        this.restTemplate = restTemplate;
    }



    public Pedido crearPedido(CrearPedidoRequest request) {
        ProductoDTO producto;
        try{
            producto= restTemplate.getForObject(ID_PRODUCTO, ProductoDTO.class, request.productoId());
        } catch (HttpClientErrorException.NotFound e) {
            throw new ProductoNotFoundException("El producto no existe");
        }
        if(Boolean.FALSE.equals(producto.estado())){
            throw new ProductoNotAvailableException("El producto no esta disponible");
        }
        try{
             restTemplate.getForObject(ID_USUARIO, UsuarioDTO.class,request.usuarioId());
        }
        catch (HttpClientErrorException.NotFound e){
            throw new UsuarioNotFoundException("El usuario no existe");
        }

        if(request.cantidad()==null||request.cantidad()<=0){
            throw new IllegalArgumentException("La cantidad debe ser mayor que 0");
        }
        if(producto.stock()< request.cantidad()){
            throw new StockInsuficienteException("Stock insuficiente");
        }



        restTemplate.patchForObject(STOCK_PRODUCTO, new DescontarStockRequest(request.cantidad()), Void.class,request.productoId());
        return pedidoRepository.save(new Pedido(request.productoId(), request.usuarioId(), request.cantidad()));

    }

    public Pedido buscarPedido(Long id) {
        return pedidoRepository.findPedidoById(id);
    }

    public ArrayList<Pedido> buscarPedidoPorUsuario(Long id) {
        return pedidoRepository.findPedidoByUsuarioId(id);
    }
    public ArrayList<Pedido> buscarPedidos(Long usuarioId) {
        return (usuarioId!=null)
                ?this.buscarPedidoPorUsuario(usuarioId)
                :pedidoRepository.findAll();
    }

}
