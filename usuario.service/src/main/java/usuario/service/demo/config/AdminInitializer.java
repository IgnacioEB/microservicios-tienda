package usuario.service.demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import usuario.service.demo.model.Usuario;
import usuario.service.demo.repository.UsuarioRepository;

// Crea (o promueve) el primer ADMIN al arrancar, con ADMIN_EMAIL y ADMIN_PASSWORD.
// Si las variables no estan definidas, no hace nada.
@Component
public class AdminInitializer implements ApplicationRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final String adminEmail;
    private final String adminPassword;

    public AdminInitializer(UsuarioRepository usuarioRepository,
                            PasswordEncoder passwordEncoder,
                            @Value("${admin.email:}") String adminEmail,
                            @Value("${admin.password:}") String adminPassword) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.adminEmail = adminEmail;
        this.adminPassword = adminPassword;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (adminEmail.isBlank() || adminPassword.isBlank()) {
            return;
        }

        Usuario existente = usuarioRepository.findUsuarioByEmail(adminEmail).orElse(null);

        if (existente == null) {
            usuarioRepository.save(new Usuario(adminEmail, passwordEncoder.encode(adminPassword), Usuario.Rol.ADMIN));
        } else if (existente.getRol() != Usuario.Rol.ADMIN) {
            existente.setRol(Usuario.Rol.ADMIN);
            usuarioRepository.save(existente);
        }
    }
}
