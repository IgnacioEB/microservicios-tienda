package pedido.service.demo.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import pedido.service.demo.dto.DescontarStockRequest;
import pedido.service.demo.dto.ProductoDTO;
import pedido.service.demo.dto.UsuarioDTO;
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



    public Pedido crearPedido(Long productoId, Long usuarioId,Integer cantidad) {
        ProductoDTO producto;
        try{
            producto= restTemplate.getForObject(ID_PRODUCTO, ProductoDTO.class, productoId);
        } catch (HttpClientErrorException.NotFound e) {
            throw new ProductoNotFoundException("El producto no existe");
        }
        UsuarioDTO usuario;
        try{
        usuario= restTemplate.getForObject(ID_USUARIO, UsuarioDTO.class,usuarioId);
        }
        catch (HttpClientErrorException.NotFound e){
            throw new UsuarioNotFoundException("El usuario no existe");
        }

        if(cantidad==null||cantidad<=0){
            throw new IllegalArgumentException("La cantidad debe ser mayor que 0");
        }
        if(producto.stock()<cantidad){
            throw new StockInsuficienteException("Stock insuficiente");
        }



        restTemplate.patchForObject(STOCK_PRODUCTO, new DescontarStockRequest(cantidad), Void.class,productoId);
        return pedidoRepository.save(new Pedido(productoId,usuarioId,cantidad));

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
