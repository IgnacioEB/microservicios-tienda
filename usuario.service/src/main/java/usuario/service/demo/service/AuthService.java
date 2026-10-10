package usuario.service.demo.service;


import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import usuario.service.demo.dto.LoginRequest;
import usuario.service.demo.dto.RegistroRequest;
import usuario.service.demo.exception.CredencialesInvalidasException;
import usuario.service.demo.exception.EmailNotAvailableException;
import usuario.service.demo.model.Usuario;
import usuario.service.demo.repository.UsuarioRepository;
import usuario.service.demo.security.JwtService;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UsuarioRepository usuarioRepository, JwtService jwtService, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    public void registrar(RegistroRequest request){
        if(usuarioRepository.existsUsuarioByEmail((request.getEmail()))){
            throw new EmailNotAvailableException("El email ya esta en uso");
        }
        String hashPassword = passwordEncoder.encode(request.getPassword());
        Usuario usuario= new Usuario(request.getEmail(), request.getPassword(), Usuario.Rol.USER);
        usuarioRepository.save(usuario);
    }

    public String login(LoginRequest request){
        Usuario usuario= usuarioRepository
                .findUsuarioByEmail(request.getEmail())
                .orElseThrow(()->new CredencialesInvalidasException("Email o contraseña incorrecta"));
        if(!passwordEncoder.matches(request.getPassword(), usuario.getPassword())){
            throw new CredencialesInvalidasException(("Email o contraseña incorrecta"));
        }
        return jwtService.generarToken(usuario.getEmail(), usuario.getRol().name());
    }

}
