package usuario.service.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import usuario.service.demo.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Usuario findUsuarioByid(Long id);
}
