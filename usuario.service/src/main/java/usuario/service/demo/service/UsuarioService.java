package usuario.service.demo.service;

import org.springframework.stereotype.Service;
import usuario.service.demo.exception.UsuarioNoEncontradoException;
import usuario.service.demo.model.Usuario;
import usuario.service.demo.repository.UsuarioRepository;


@Service
public class UsuarioService {
    private UsuarioRepository usuarioRepository;
    public UsuarioService(UsuarioRepository usuarioRepository){
        this.usuarioRepository=usuarioRepository;
    }
    public UsuarioService(){}


    public void registrarUsuario(String nombre, String email){
        usuarioRepository.save(new Usuario(nombre,email));
    }

    public Usuario obtenerUsuario(Long id){
        Usuario user= usuarioRepository.findUsuarioByid(id);
        if(user==null){
            throw new UsuarioNoEncontradoException("Usuario no encontrado");
        }
        return user;
    }








}
