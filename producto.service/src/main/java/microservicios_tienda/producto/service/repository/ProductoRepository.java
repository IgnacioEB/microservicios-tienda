package microservicios_tienda.producto.service.repository;

import jakarta.transaction.Transactional;
import microservicios_tienda.producto.service.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.jpa.repository.Query;

import java.util.ArrayList;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    Producto findProductoById(Long id);

    ArrayList<Producto> findAll();

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("UPDATE productos  p SET p.stock=(p.stock-?2) WHERE p.id=?1")
    void descontarStock(Long id, Integer cantidad);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("UPDATE productos p SET p.estado=false WHERE p.id=?1")
    void desactivar(Long id);



}
