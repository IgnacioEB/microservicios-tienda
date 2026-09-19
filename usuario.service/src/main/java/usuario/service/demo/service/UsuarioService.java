package usuario.service.demo.service;

import org.springframework.stereotype.Service;
import usuario.service.demo.dto.UsuarioPutRequest;
import usuario.service.demo.exception.UsuarioNoEncontradoException;
import usuario.service.demo.model.Usuario;
import usuario.service.demo.repository.UsuarioRepository;

import java.util.ArrayList;


@Service
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    public UsuarioService(UsuarioRepository usuarioRepository){
        this.usuarioRepository=usuarioRepository;
    }



    public Usuario registrarUsuario(String nombre, String email){
        return usuarioRepository.save(new Usuario(nombre, email));
    }

    public Usuario obtenerUsuario(Long id){
        Usuario user= usuarioRepository.findUsuarioByid(id);
        if(user==null){
            throw new UsuarioNoEncontradoException("Usuario no encontrado");
        }
        return user;
    }

    public ArrayList<Usuario> obtenerUsuarios(){
        return usuarioRepository.obtenerUsuarios();
    }


    public Usuario modificarUsuario(Long id,UsuarioPutRequest request){
        if(usuarioRepository.findUsuarioByid(id)==null){
            throw new UsuarioNoEncontradoException("Usuario no encontrado");
        }
        usuarioRepository.modificarUsuario(id,request.getNombre(),request.getEmail());
        return usuarioRepository.findUsuarioByid(id);
    }


    public void eliminarUsuario(Long id){
        if(usuarioRepository.findUsuarioByid(id)==null){
            throw new UsuarioNoEncontradoException("Usuaro no encontrado");
        }
        usuarioRepository.deleteUsuarioById(id);
    }
}
