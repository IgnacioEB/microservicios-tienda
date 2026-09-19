package usuario.service.demo.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.jpa.repository.Query;
import usuario.service.demo.model.Usuario;

import java.util.ArrayList;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Usuario findUsuarioByid(Long id);

    @NativeQuery(value = "SELECT * FROM usuarios ORDER BY id")
    ArrayList<Usuario> obtenerUsuarios();

    @Modifying
    @Transactional
    @Query("UPDATE usuarios u SET u.nombreCompleto=?2, u.email=?3 WHERE u.id=?1")
    void modificarUsuario(Long id, String nombre, String email);

    @Modifying
    @Transactional
    void deleteUsuarioById(Long id);
}
