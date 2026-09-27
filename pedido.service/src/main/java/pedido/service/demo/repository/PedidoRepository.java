package pedido.service.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pedido.service.demo.model.Pedido;

import java.util.ArrayList;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {


    Pedido findPedidoById(Long id);

    ArrayList<Pedido> findPedidoByUsuarioId(Long id);

    ArrayList<Pedido> findAll();
}
